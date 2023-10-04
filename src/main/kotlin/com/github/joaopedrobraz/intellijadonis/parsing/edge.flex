// Based on https://github.com/edge-js/lexer, https://github.com/edge-js/parser and https://github.com/edge-js/edge

package com.github.joaopedrobraz.intellijadonis.parsing;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// suppress various warnings/inspections for the generated class
@SuppressWarnings ({"FieldCanBeLocal", "UnusedDeclaration", "UnusedAssignment", "WeakerAccess", "SameParameterValue", "CanBeFinal", "SameReturnValue", "RedundantThrows", "ConstantConditions"})
%%

%class _EdgeLexer
%implements FlexLexer
%final
%unicode
%function advance
%type IElementType

%{
    private static final HashMap<EdgeValidTags, Integer> CUSTOM_TAG_BEHAVIOR = new HashMap<>();
    static {
        CUSTOM_TAG_BEHAVIOR.put(EdgeValidTags.EACH, tag_content_each);
    }

    private int parenthesesCount = 0;
    private IElementType contentToReturn = null;
    private String currentTag = "";

    private IElementType contentOrWhiteSpace(IElementType _contentToReturn) {
        return !yytext().toString().isBlank() ? _contentToReturn : EdgeTokenTypes.WHITE_SPACE;
    }

    private IElementType contentOrWhiteSpace() {
        return contentOrWhiteSpace(EdgeTokenTypes.HTML_CONTENT);
    }

    private void clearWhiteSpace() {
        int charsToPushBack = 0;
        for (int i = yylength() - 1; i > 0; i--) {
            char currentChar = yytext().charAt(i);
            if (String.valueOf(currentChar).isBlank()) charsToPushBack++;
            else break;
        }
        yypushback(charsToPushBack);
    }

    private String collectTill(CharSequence source, String stops) {
        StringBuilder collectedString = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            char currentChar = source.charAt(i);
            if (stops.contains(String.valueOf(currentChar))) break;
            collectedString.append(currentChar);
        }
        return collectedString.toString();
    }
%}

LineTerminator = \r|\n|\r\n
WhiteSpace = {LineTerminator} | [ \t\f]
Tag = "@"
EscapedTag = "@@"
OpenCurlyBraces = "{{"
OpenEscapedCurlyBraces = "@{{"
OpenRawCurlyBraces = "{{{"
OpenEscapedRawCurlyBraces = "@{{{"
OpenComment = "{{--"
// Not really correct, but maybe good enough? (see https://stackoverflow.com/questions/1661197/what-characters-are-valid-for-javascript-variable-names)
ValidJavaScriptIdentifier = [a-zA-Z_$][0-9a-zA-Z_$]*
ValidTagIdentifier = [a-zA-Z._]+
ValidTag = {Tag}\!?{ValidTagIdentifier}\s{0,2}
ValidPartialTag = {Tag}\!?\s{0,2}({WhiteSpace}+|[\W]*)
// Check for anything that is not a string containing "{{" or "@{{" or "@@" or "@"; that's CONTENT
Content = !([^]*({OpenCurlyBraces}|{OpenRawCurlyBraces}|{OpenEscapedCurlyBraces}|{OpenEscapedRawCurlyBraces}|{EscapedTag}|{Tag})[^]*)

%state tag
%state tag_content
%state tag_content_each
%state tag_identifier
%state curly_braces
%state raw_curly_braces
%state comment
%state content
%state swallow_new_line
%state YYINITIAL_NO_TAG

%%

<YYINITIAL> {
    // Simulate a lookahead/lookbehind to determine whether this "@" is part of a valid tag or just a random @ in the source code
    ~"@" {
          boolean abort = false;
          // Lookbehind, there must not be any of the "invalidTokens"
          String[] invalidTokens = { "@{{{", "@{{", "{{{", "{{" };
          String textMatch = yytext().toString();
          for (String invalidToken : invalidTokens) {
              int indexOfMatch = textMatch.indexOf(invalidToken);
              if (indexOfMatch > -1) {
                  yypushback(yylength());
                  yybegin(YYINITIAL_NO_TAG);
                  abort = true;
                  break;
              }
          }

          if (!abort) {
              boolean isValid = true;
              // Lookbehind, it must either be the beginning of the file OR a line break, optionally with white space
              if (yylength() > 1) {
                  for (int i = yylength() - 2; i > 0; i--) {
                      char currentChar = yytext().charAt(i);
                      if (currentChar == '\n' || currentChar == '\r') break;
                      if (!String.valueOf(currentChar).isBlank()) {
                          isValid = false;
                          break;
                      }
                  }
              }

              if (!isValid) {
                  if (yylength() > 0) return contentOrWhiteSpace();
              }

              // Lookahead, it must match a valid tag:
              // (\(.*?\)?)?~? (\s*$|\s*(\n\r|\r|\n))
              // !?<Identifier>\s{0,2}:
              //                       ~?<EOF|WHITESPACE>
              //                       (<CONTENT>*)<EOF|WHITESPACE>
              //                       (<CONTENT>*)~<EOF|WHITESPACE>
              //                       (<CONTENT>*<EOF|WHITESPACE>
              Pattern pattern = Pattern.compile("^!?[a-zA-Z._]+\\s{0,2}((\\(.*?\\))(\\s*$|\\s*(\\n\\r|\\r|\\n))|(\\(.*?\\))~(\\s*$|\\s*(\\n\\r|\\r|\\n))|(\\([^)]*)(\\s*$|\\s*(\\n\\r|\\r|\\n))|~?(\\s*$|\\s*(\\n\\r|\\r|\\n)))");
              String potentialTag = collectTill(zzBufferL.subSequence(zzMarkedPos, zzBufferL.length()), "\r\n");
              if (potentialTag.isBlank()) {
                  // A solitary tag
                  if (yytext().toString().trim().equals("@")) {
                      clearWhiteSpace();
                      return EdgeTokenTypes.TAG;
                  }
                  yypushback(1);
                  yybegin(tag);
                  if (yylength() > 0) return contentOrWhiteSpace();
              }
              Matcher matcher = pattern.matcher(potentialTag);
              if (!matcher.find()) {
                  if (yylength() > 0) return contentOrWhiteSpace();
              }

              // Yay! This is a valid tag
              yypushback(1);
              yybegin(tag);
              if (yylength() > 0) return contentOrWhiteSpace();
          }
    }
}

<YYINITIAL, YYINITIAL_NO_TAG> {
    {EscapedTag} { return EdgeTokenTypes.HTML_CONTENT; }

    {OpenRawCurlyBraces} {
              yybegin(raw_curly_braces);
              contentToReturn = EdgeTokenTypes.JAVASCRIPT_CONTENT;
              return EdgeTokenTypes.OPEN_RAW_CURLY_BRACES;
          }

    {OpenEscapedRawCurlyBraces} {
              yybegin(raw_curly_braces);
              contentToReturn = EdgeTokenTypes.HTML_CONTENT;
              return EdgeTokenTypes.ESCAPED_OPEN_RAW_CURLY_BRACES;
          }

    {OpenCurlyBraces} {
              yybegin(curly_braces);
              contentToReturn = EdgeTokenTypes.JAVASCRIPT_CONTENT;
              return EdgeTokenTypes.OPEN_CURLY_BRACES;
          }

    {OpenEscapedCurlyBraces} {
              yybegin(curly_braces);
              contentToReturn = EdgeTokenTypes.HTML_CONTENT;
              return EdgeTokenTypes.ESCAPED_OPEN_CURLY_BRACES;
          }

    {OpenComment} { yybegin(comment); return EdgeTokenTypes.OPEN_COMMENT; }

    {Content} {
          String text = yytext().toString();
          if (yylength() > 1 && yycharat(yylength() - 1) == '{') yypushback(1);
          return contentOrWhiteSpace();
    }
}

<content> {
    {Content} { return contentOrWhiteSpace(); }
}

<tag> {
    {ValidTag} {
          yybegin(tag_identifier);
          if (yytext().toString().startsWith("@!")) {
              yypushback(yylength() - 2);
              return EdgeTokenTypes.SELF_CLOSING_TAG;
          } else {
              yypushback(yylength() - 1);
              return EdgeTokenTypes.TAG;
          }
    }

    {ValidPartialTag} { yybegin(YYINITIAL); clearWhiteSpace(); return EdgeTokenTypes.TAG; }

    // REMOVED: . {  yypushback(1); yybegin(content); }
}

<tag_identifier> {
    {ValidTagIdentifier} {
              clearWhiteSpace();
              currentTag = yytext().toString();
              return EdgeTokenTypes.IDENTIFIER;
        }

    \(.* {
          yypushback(yylength() - 1);
          yybegin(CUSTOM_TAG_BEHAVIOR.getOrDefault(EdgeValidTags.fromString(currentTag), tag_content));
          parenthesesCount = 1;
          return EdgeTokenTypes.OPEN_PARENTHESES;
    }

    \~ { yypushback(1); yybegin(swallow_new_line); }

    . { yypushback(1); yybegin(YYINITIAL); }
}

<tag_content> {
    .+ {
              if (parenthesesCount == 0) {
                  clearWhiteSpace();
                  if (yytext().toString().endsWith("~")) {
                      yypushback(1);
                      yybegin(swallow_new_line);
                  } else yybegin(YYINITIAL);
                  return EdgeTokenTypes.CLOSE_PARENTHESES;
              }

              clearWhiteSpace();
              if (!yytext().toString().endsWith(")") && !yytext().toString().endsWith(")~")) {
                  yypushback(yylength());
                  yybegin(YYINITIAL);
              } else {
                  int matchedChars = 0;
                  for (int i = 0; i < yylength(); i++) {
                      char currentChar = yytext().charAt(i);
                      parenthesesCount += currentChar == '(' ? 1 : (currentChar == ')' ? -1 : 0);
                      if (parenthesesCount == 0) {
                          yypushback(yylength() - matchedChars);
                          return EdgeTokenTypes.JAVASCRIPT_CONTENT;
                      }
                      matchedChars++;
                  }
              }
          }
}

<tag_content_each> {
    {ValidJavaScriptIdentifier} {
              int matchedChars = 0;
              for (int i = 0; i < yylength(); i++) {
                  char currentChar = yytext().charAt(i);
                  if (String.valueOf(currentChar).isBlank()) {
                      break;
                  }
                  matchedChars++;
              }
              yypushback(yylength() - matchedChars);
              return EdgeTokenTypes.IDENTIFIER;
          }

    \( { return EdgeTokenTypes.OPEN_PARENTHESES; }

    \) { return EdgeTokenTypes.CLOSE_PARENTHESES; }

    \)\~ {  yypushback(1); yybegin(swallow_new_line); return EdgeTokenTypes.CLOSE_PARENTHESES; }

    \s*\,\s* { return EdgeTokenTypes.WHITE_SPACE; }

    \s+"in"\s+ { yybegin(tag_content); return EdgeTokenTypes.KEYWORD; }

    . { yypushback(yylength()); yybegin(YYINITIAL); }
}

<curly_braces> {
    "}}" { yybegin(YYINITIAL); return EdgeTokenTypes.CLOSE_CURLY_BRACES; }
    ~"}}" { yypushback(2); return contentOrWhiteSpace(contentToReturn); }
    . { /* If here, we have an unclosed braces */ yypushback(yylength()); yybegin(YYINITIAL); }
}

<raw_curly_braces> {
    "}}}" { yybegin(YYINITIAL); return EdgeTokenTypes.CLOSE_RAW_CURLY_BRACES; }
    ~"}}}" { yypushback(3); return contentOrWhiteSpace(contentToReturn); }
    . { /* If here, we have an unclosed braces */ yypushback(yylength()); yybegin(YYINITIAL); }
}

<comment> {
    "--}}" {  yybegin(YYINITIAL); return EdgeTokenTypes.CLOSE_COMMENT; }
    ~"--}}" { yypushback(4); return contentOrWhiteSpace(EdgeTokenTypes.COMMENT_CONTENT); }
    . { /* If here, we have an unclosed braces */ yypushback(yylength()); yybegin(YYINITIAL); }
}

<swallow_new_line> {
    . { yybegin(YYINITIAL); return EdgeTokenTypes.COMMENT_CONTENT; }
}

{WhiteSpace}+ { return EdgeTokenTypes.WHITE_SPACE; }
[^] { return EdgeTokenTypes.INVALID; }
