package fr.ufrst.m1info.projetcomp.m1comp2.gui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.GenericStyledArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.collection.ListModification;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SyntaxHighlight {
    final public Color BREAKPOINT_COLOR = Color.rgb(199, 84, 80);

    private static final String[] KEYWORDS = new String[] {
            "boolean", "class", "else", "final", "if", "int", "main", "return", "void", "while"
    };
    private static final String[] OPERATORS = new String[] {
            "\\+", "\\-", "\\*", "\\/(?!\\/|\\*)", "\\+\\+", "\\+\\=", "\\=", "\\!", "\\&\\&", "\\|\\|", "\\=\\=", "\\>"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String OPERATOR_PATTERN = String.join("|", OPERATORS);
    private static final String FUNCTION_PATTERN = "(?!if|while)(\\w+)\\s*(?=\\([^)]*\\))"; //"(?!if|while)(\\w+)\\s*\\([^)]*\\)\\s*";
    private static final String BOOLEAN_PATTERN = "true|false";
    private static final String VARIABLE_PATTERN = "([a-z]|_)([a-z]|[0-9]|_|\\-)*";
    private static final String INTEGER_PATTERN = "[\\-]?[0-9]+"; // [^abcdefghijklmnopqrstuvwxyz_][\+\-]?[0-9]+
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;|,";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"|(\")";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/"   // for whole text processing (text blocks)
            + "|" + "/\\*[^\\v]*" + "|" + "^\\h*\\*([^\\v]*|/)";  // for visible paragraph processing (line by line)
    private static final String SUCCEED_PATTERN = "Compilation succeed.*|Interpretation succeed.*";
    private static final String ERROR_PATTERN = ".*Error.*|.*expecting.*|.*\\.\\.\\.|;|The compilation failed.*|The interpretation failed.*";
    private static final String WARNING_PATTERN = ".*Warning.*";

    private static final Pattern CODE_PATTERN = Pattern.compile(
            // For code area
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
            + "|(?<OPERATOR>" + OPERATOR_PATTERN + ")"
            + "|(?<FUNCTION>" + FUNCTION_PATTERN + ")"
            + "|(?<BOOLEAN>" + BOOLEAN_PATTERN + ")"
            + "|(?<VARIABLE>" + VARIABLE_PATTERN + ")"
            + "|(?<INTEGER>" + INTEGER_PATTERN + ")"
            + "|(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<BRACE>" + BRACE_PATTERN + ")"
            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );
    private static final Pattern OUTPUT_PATTERN = Pattern.compile(
            // For output area
            "(?<SUCCEED>" + SUCCEED_PATTERN + ")"
            + "|(?<ERROR>" + ERROR_PATTERN + ")"
            + "|(?<WARNING>" + WARNING_PATTERN + ")"
    );

    void initCodeArea(CodeArea miniJajaTextArea, Set<Integer> breakPoints) {
        setBreakPointAndLines(miniJajaTextArea, breakPoints);

        miniJajaTextArea.getVisibleParagraphs().addModificationObserver(new VisibleParagraphStyler<>(miniJajaTextArea, this::computeHighlightingCodeArea));

        final Pattern whiteSpace = Pattern.compile( "^\\s+" );
        miniJajaTextArea.addEventHandler( KeyEvent.KEY_PRESSED, KE ->
        {
            if ( KE.getCode() == KeyCode.ENTER ) {
                int caretPosition = miniJajaTextArea.getCaretPosition();
                int currentParagraph = miniJajaTextArea.getCurrentParagraph();
                Matcher m0 = whiteSpace.matcher( miniJajaTextArea.getParagraph( currentParagraph-1 ).getSegments().get( 0 ) );
                if ( m0.find() ) Platform.runLater( () -> miniJajaTextArea.insertText( caretPosition, m0.group() ) );
            }
        });
    }

    void setBreakPointAndLines(CodeArea codeArea, Set<Integer> breakPoints) {
        IntFunction<Node> numberFactory = LineNumberFactory.get(codeArea);
        IntFunction<Node> graphicFactory = lineIndex -> {
            Node lineNumberText = numberFactory.apply(lineIndex);

            int numLine = lineIndex + 1;
            Circle breakPoint = new Circle(6);
            breakPoint.setFill(breakPoints.contains(numLine) ? BREAKPOINT_COLOR : Color.TRANSPARENT);

            HBox hbox = new HBox(lineNumberText, breakPoint);
            Tooltip tooltip = new Tooltip("Set breakpoint");
            Tooltip.install(hbox, tooltip);

            hbox.setAlignment(Pos.CENTER_LEFT);
            hbox.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
            hbox.setPadding(new Insets(0, 6, 0, 0));
            hbox.setStyle("-fx-border-style: hidden solid hidden hidden;-fx-border-width: 0.5;-fx-border-color: #868481;");

            hbox.setOnMouseClicked((click) -> {
                Circle breakCircle = (Circle) hbox.getChildren().get(1);

                if (breakPoints.contains(numLine)) {
                    breakPoints.remove(numLine);
                    breakCircle.setFill(Color.TRANSPARENT);
                } else {
                    breakPoints.add(numLine);
                    breakCircle.setFill(BREAKPOINT_COLOR);
                }
            });
            hbox.setCursor(Cursor.HAND);

            return hbox;
        };
        codeArea.setParagraphGraphicFactory(graphicFactory);
    }

    void initOutputArea(CodeArea outputArea) {
        outputArea.getVisibleParagraphs().addModificationObserver(new VisibleParagraphStyler<>(outputArea, this::computeHighlightingOutputArea));
    }

    void initJajaCode(CodeArea codeArea, Set<Integer> breakPoints) {
        codeArea.setEditable(false);
        setBreakPointAndLines(codeArea, breakPoints);
    }

    private StyleSpans<Collection<String>> computeHighlightingCodeArea(String text) {
        Matcher matcher = CODE_PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("OPERATOR") != null ? "operator" :
                                    matcher.group("FUNCTION") != null ? "function" :
                                            matcher.group("BOOLEAN") != null ? "boolean" :
                                                    matcher.group("VARIABLE") != null ? "variable" :
                                                            matcher.group("INTEGER") != null ? "integer" :
                                                                    matcher.group("PAREN") != null ? "paren" :
                                                                            matcher.group("BRACE") != null ? "brace" :
                                                                                    matcher.group("BRACKET") != null ? "bracket" :
                                                                                            matcher.group("SEMICOLON") != null ? "semicolon" :
                                                                                                    matcher.group("STRING") != null ? "string" :
                                                                                                            matcher.group("COMMENT") != null ? "comment" :
                                                                                                                    null; /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    private StyleSpans<Collection<String>> computeHighlightingOutputArea(String text) {
        Matcher matcher = OUTPUT_PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
                    matcher.group("SUCCEED") != null ? "succeed" :
                            matcher.group("ERROR") != null ? "error" :
                                    matcher.group("WARNING") != null ? "warning" :
                                             null; /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    private class VisibleParagraphStyler<PS, SEG, S> implements Consumer<ListModification<? extends Paragraph<PS, SEG, S>>> {
        private final GenericStyledArea<PS, SEG, S> area;
        private final Function<String,StyleSpans<S>> computeStyles;
        private int prevParagraph, prevTextLength;

        public VisibleParagraphStyler(GenericStyledArea<PS, SEG, S> area, Function<String,StyleSpans<S>> computeStyles) {
            this.computeStyles = computeStyles;
            this.area = area;
        }

        @Override
        public void accept(ListModification<? extends Paragraph<PS, SEG, S>> lm) {
            if (lm.getAddedSize() > 0) {
                int paragraph = Math.min( area.firstVisibleParToAllParIndex() + lm.getFrom(), area.getParagraphs().size()-1 );
                String text = area.getText(paragraph, 0, paragraph, area.getParagraphLength(paragraph));

                if (paragraph != prevParagraph || text.length() != prevTextLength) {
                    int startPos = area.getAbsolutePosition(paragraph, 0);
                    Platform.runLater(() -> area.setStyleSpans(startPos, computeStyles.apply(text)));
                    prevTextLength = text.length();
                    prevParagraph = paragraph;
                }
            }
        }
    }

    public void highlightErrorLine(CodeArea miniJajaTextArea, int lineNumber) {
        // TODO (for release 2)
    }
}
