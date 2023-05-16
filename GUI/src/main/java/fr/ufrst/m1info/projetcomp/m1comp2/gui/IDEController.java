package fr.ufrst.m1info.projetcomp.m1comp2.gui;

import fr.ufrst.m1info.projetcomp.m1comp2.Memory;
import fr.ufrst.m1info.projetcomp.m1comp2.Quad;
import fr.ufrst.m1info.projetcomp.m1comp2.SymbolTable;
import fr.ufrst.m1info.projetcomp.m1comp2.analyser.mjj.ParseException;
import fr.ufrst.m1info.projetcomp.m1comp2.analyser.mjj.TokenMgrError;
import fr.ufrst.m1info.projetcomp.m1comp2.interpreters.mjj.InterpreterMjj;
import fr.ufrst.m1info.projetcomp.m1comp2.interpreters.shared.Debugger;
import fr.ufrst.m1info.projetcomp.m1comp2.interpreters.shared.Observer;
import fr.ufrst.m1info.projetcomp.typecheker.TypeChecker;
import fr.ufrst.m1info.projetcomp.m1comp2.analyser.mjj.MiniJaja;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.SimpleNode;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.VisitorException;
import fr.ufrst.m1info.projetcomp.m1comp2.compiler.Compiler;
import fr.ufrst.m1info.projetcomp.m1comp2.compiler.CompilerToString;
import fr.ufrst.m1info.projetcomp.m1comp2.interpreters.jjc.InterpreterJjc;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.*;
import java.util.*;
public class IDEController implements Observer {
    private final String ALERT_QUIT_IDE = "ALERT_QUIT_IDE";
    private final String ALERT_CLOSE_FILE = "ALERT_CLOSE_FILE";
    private final String ALERT_CLOSE_FILE_BEFORE_CLOSE = "ALERT_CLOSE_FILE_BEFORE_CLOSE";
    private final Image checked = new Image(IDEApplication.class.getResource("icons/checkbox.png").toExternalForm());
    private final Image checkedLight = new Image(IDEApplication.class.getResource("icons/checkbox_light.png").toExternalForm());
    public MenuItem jajaCodeMenuItem;
    public MenuItem memoryMenuItem;
    public MenuItem miniJajaConsoleMenuItem;
    public MenuItem jajaCodeConsoleMenuItem;
    public MenuItem darkmodeMenuItem;
    private final int HEIGHT_ROW_STACK = 25;
    private final SyntaxHighlight syntax = new SyntaxHighlight();
    private final OutputConsole miniJajaOutput = new OutputConsole("Interpretation");
    private final OutputConsole jajaCodeOutput = new OutputConsole("Compilation");
    public ButtonBar buttonBar;
    public Button compileBtn;
    public MenuButton debugBtn;
    public Button debugStepIntoBtn;
    public Button debugStepOverBtn;
    public MenuButton runBtn;
    public Button stopBtn;
    public SplitPane consoleSplitPanel;
    public BorderPane mainParentPanel;
    public MenuItem closeFileBtn;
    public MenuItem saveFileBtn;
    public MenuItem saveFileAsBtn;
    private IDEModel model;
    private Stage stage;
    private TextFile currentTextFile;
    private boolean jajaCodePanelIsDisplay;
    private boolean consolePanelIsDisplay;
    private boolean consoleMiniJajaPanelIsDisplay;
    private boolean consoleJajaCodePanelIsDisplay;
    private boolean memoryPanelIsDisplay;
    private boolean toogleDebugOption;
    private boolean fileOpened = false;
    public boolean darkmode;
    private Set<Integer> breakPointsMiniJaja;
    private Set<Integer> breakPointsJajaCode;

    public SplitPane mainSplitPanel;
    public SplitPane secondSplitPanel;
    @FXML
    private BorderPane jajaCodePanel;
    @FXML
    private BorderPane consoleMiniJajaPanel;
    @FXML
    private BorderPane consoleJajaCodePanel;
    @FXML
    private BorderPane memoryPanel;
    @FXML
    private CodeArea miniJajaTextArea;
    @FXML
    private CodeArea jajaCodeTextArea;
    @FXML
    private VBox memoryGridPane;
    @FXML
    private CodeArea miniJajaOutputArea;
    @FXML
    private CodeArea jajaCodeOutputArea;

    // Compilation / Interpretation
    private List<fr.ufrst.m1info.projetcomp.m1comp2.ast.jjc.Node> interpretationInstructions;
    private InterpreterJjc interpreterJjc;
    private InterpreterMjj interpreterMjj;
    private Debugger debugger;
    private SimpleNode root;
    private Compiler compiler;
    private TypeChecker typeChecker;
    private SymbolTable symbolTable;
    private Memory memory;

    public IDEController(IDEModel model, Stage stage) {
        this.model = model;
        this.stage = stage;
        miniJajaTextArea = new CodeArea();
        jajaCodeTextArea = new CodeArea();
        breakPointsMiniJaja = new HashSet<>();
        breakPointsJajaCode = new HashSet<>();
        jajaCodePanelIsDisplay = true;
        consolePanelIsDisplay = true;
        consoleMiniJajaPanelIsDisplay = true;
        consoleJajaCodePanelIsDisplay = false;
        memoryPanelIsDisplay = true;
        darkmode = false;
        toogleDebugOption = false;
    }

    /**
     * File Actions
     */

    private void close() {
        miniJajaTextArea.clear();
        jajaCodeTextArea.clear();
        fileOpened = false;
        toggleFileOptionIfOpen();
        // Update the window title with the default title
        stage.setTitle("MiniJaja IDE");
        miniJajaTextArea.setVisible(false);
        jajaCodeTextArea.setVisible(false);
    }

    @FXML
    protected void newFile() {
        if (fileOpened) {
            createPopUpAlertType(ALERT_CLOSE_FILE_BEFORE_CLOSE);
        }
        clearBreakPoints();
        saveFileAs();
        miniJajaTextArea.clear();
        jajaCodeTextArea.clear();
        fileOpened = true;
        toggleFileOptionIfOpen();
    }

    private void openInIDE(File file) {
        clearBreakPoints();
        IOResult<TextFile> io = model.openFile(file.toPath());
        if (io.isOk()) {
            currentTextFile = io.getData();
            miniJajaTextArea.clear();
            currentTextFile.getContent().forEach(line -> miniJajaTextArea.appendText(line + "\n"));
            fileOpened = true;
            toggleFileOptionIfOpen();
            // Update the window title with the title of the current file editing
            stage.setTitle(file.getName() + " - MiniJaja IDE");
            miniJajaTextArea.scrollYToPixel(0);
            miniJajaTextArea.setVisible(true);
            jajaCodeTextArea.setVisible(true);
        }
    }

    @FXML
    protected void openFile() {
        clearBreakPoints();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./../..")); // Can be changed later
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MiniJaja doc(*.mjj)", "*.mjj"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            openInIDE(file);
        }
    }

    @FXML
    protected void saveFile() {
        if (fileOpened) {
            model.saveFile(miniJajaTextArea.getText(), currentTextFile.getFile().toString());
        } else {
            saveFileAs();
        }
    }

    @FXML
    protected void saveFileAs() {
        String textContent = miniJajaTextArea.getText();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./../..")); // Can be changed later
        fileChooser.setInitialFileName("untitled.mjj");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MiniJaja doc(*.mjj)", "*.mjj"));
        File newFile = fileChooser.showSaveDialog(null);

        if (newFile != null) {
            try {
                PrintWriter writer;
                writer = new PrintWriter(newFile);
                writer.println(textContent);
                writer.close();
            } catch (IOException ex) {
                //Logger.getLogger(SaveFileWithFileChooser.class.getName()).log(Level.SEVERE, null, ex);
            }
            openInIDE(newFile);
        }
    }

    @FXML
    protected void closeFile() {
        if (fileOpened) {
            createPopUpAlertType(ALERT_CLOSE_FILE);
        }
    }

    @FXML
    protected void quitIde() {
        if (fileOpened) {
            createPopUpAlertType(ALERT_QUIT_IDE);
        } else {
            model.closeIDE();
        }
    }

    private void createPopUpAlertType(String type) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Save file");
        if (type.equals(ALERT_CLOSE_FILE_BEFORE_CLOSE)) {
            alert.setContentText("Do you want to save this file before creating a new one?");
        } else {
            alert.setContentText("Do you want to save this file before closing it?");
        }
        ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(okButton, noButton, cancelButton);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == okButton) {
            if (type.equals(ALERT_CLOSE_FILE)) {
                clearBreakPoints();
                saveFile();
                close();
            } else if (type.equals(ALERT_QUIT_IDE)) {
                saveFile();
                model.closeIDE();
            }
        } else if (result.get() == noButton) {
            if (type.equals(ALERT_CLOSE_FILE)) {
                clearBreakPoints();
                close();
            } else if (type.equals(ALERT_QUIT_IDE)) {
                model.closeIDE();
            }
        }
        else if (result.get() == cancelButton) {
            alert.close();
        }
    }

    /**
     * Edit Actions
     */

    @FXML
    protected void undo() {
        miniJajaTextArea.undo();
    }


    @FXML
    protected void redo() {
        miniJajaTextArea.redo();
    }

    /**
     * Window Actions
     */

    void setIconsButton() {
        ImageView imageViewCompile = new ImageView(new Image(IDEApplication.class.getResource("icons/compile.png").toExternalForm()));
        imageViewCompile.setFitHeight(15);
        imageViewCompile.setPreserveRatio(true);
        compileBtn.setPrefSize(20, 20);
        compileBtn.setGraphic(imageViewCompile);
        ImageView imageViewDebug = new ImageView(new Image(IDEApplication.class.getResource("icons/debug.png").toExternalForm()));
        imageViewDebug.setFitHeight(15);
        imageViewDebug.setPreserveRatio(true);
        debugBtn.setPrefSize(20, 20);
        debugBtn.setGraphic(imageViewDebug);
        ImageView imageViewStepInto = new ImageView(new Image(IDEApplication.class.getResource("icons/stepInto.png").toExternalForm()));
        imageViewStepInto.setFitHeight(15);
        imageViewStepInto.setPreserveRatio(true);
        debugStepIntoBtn.setPrefSize(20, 20);
        debugStepIntoBtn.setGraphic(imageViewStepInto);
        ImageView imageViewStepOver = new ImageView(new Image(IDEApplication.class.getResource("icons/stepOver.png").toExternalForm()));
        imageViewStepOver.setFitHeight(15);
        imageViewStepOver.setPreserveRatio(true);
        debugStepOverBtn.setPrefSize(20, 20);
        debugStepOverBtn.setGraphic(imageViewStepOver);
        ImageView imageViewRun = new ImageView(new Image(IDEApplication.class.getResource("icons/run.png").toExternalForm()));
        imageViewRun.setFitHeight(15);
        imageViewRun.setPreserveRatio(true);
        runBtn.setPrefSize(20, 20);
        runBtn.setGraphic(imageViewRun);
        ImageView imageViewStop = new ImageView(new Image(IDEApplication.class.getResource("icons/stop.png").toExternalForm()));
        imageViewStop.setFitHeight(15);
        imageViewStop.setPreserveRatio(true);
        stopBtn.setPrefSize(20, 20);
        stopBtn.setGraphic(imageViewStop);
    }

    public void updateIconsMenuItem() {
        jajaCodeToogleMenuItemIcon();
        memoryToogleMenuItemIcon();
        consoleMiniJajaToogleMenuItemIcon();
        consoleJajaCodeToogleMenuItemIcon();
        darkModeToogleMenuItemIcon();
    }

    private void consoleMiniJajaToogleMenuItemIcon() {
        ImageView icon = new ImageView(consoleMiniJajaPanelIsDisplay ? (darkmode ? checkedLight : checked) : null);
        icon.setFitHeight(15);
        icon.setFitWidth(15);
        miniJajaConsoleMenuItem.setGraphic(icon);
    }

    private void consoleJajaCodeToogleMenuItemIcon() {
        ImageView icon = new ImageView(consoleJajaCodePanelIsDisplay ? (darkmode ? checkedLight : checked) : null);
        icon.setFitHeight(15);
        icon.setFitWidth(15);
        jajaCodeConsoleMenuItem.setGraphic(icon);
    }

    private void memoryToogleMenuItemIcon() {
        ImageView icon = new ImageView(memoryPanelIsDisplay ? (darkmode ? checkedLight : checked) : null);
        icon.setFitHeight(15);
        icon.setFitWidth(15);
        memoryMenuItem.setGraphic(icon);
    }

    private void jajaCodeToogleMenuItemIcon() {
        ImageView icon = new ImageView(jajaCodePanelIsDisplay ? (darkmode ? checkedLight : checked) : null);
        icon.setFitHeight(15);
        icon.setFitWidth(15);
        jajaCodeMenuItem.setGraphic(icon);
    }

    private void darkModeToogleMenuItemIcon() {
        ImageView icon = new ImageView(darkmode ? checkedLight : null);
        icon.setFitHeight(15);
        icon.setFitWidth(15);
        darkmodeMenuItem.setGraphic(icon);
    }

    void initCodeArea() {
        syntax.initCodeArea(miniJajaTextArea, breakPointsMiniJaja);
        miniJajaTextArea.setVisible(false);
        jajaCodeTextArea.setVisible(false);
    }

    void initMiniJajaOutputArea() {
        syntax.initOutputArea(miniJajaOutputArea);
        if (!consoleMiniJajaPanelIsDisplay) {
            consoleSplitPanel.getItems().remove(consoleJajaCodePanel);
        }
    }

    void initJajaCodeOutputArea() {
        syntax.initOutputArea(jajaCodeOutputArea);
        if (!consoleJajaCodePanelIsDisplay) {
            consoleSplitPanel.getItems().remove(consoleJajaCodePanel);
        }
    }

    void initJajaCodeResultArea() {
        syntax.initJajaCode(jajaCodeTextArea, breakPointsJajaCode);
        if (!jajaCodePanelIsDisplay) {
            secondSplitPanel.getItems().remove(jajaCodePanel);
        }
    }

    public void toggleJajaCodePanel() {
        if (jajaCodePanelIsDisplay) {
            secondSplitPanel.getItems().remove(jajaCodePanel);
        } else {
            secondSplitPanel.getItems().add(jajaCodePanel);
        }
        jajaCodePanelIsDisplay = !jajaCodePanelIsDisplay;
        setLengthOfPanelAuto();
        jajaCodeToogleMenuItemIcon();
    }

    public void toggleMemoryPanel() {
        if (memoryPanelIsDisplay) {
            secondSplitPanel.getItems().remove(memoryPanel);
        } else {
            secondSplitPanel.getItems().add(memoryPanel);
        }
        memoryPanelIsDisplay = !memoryPanelIsDisplay;
        setLengthOfPanelAuto();
        memoryToogleMenuItemIcon();
    }

    public void toogleConsoleSplitIfAlreadyOpen() {
        if (consoleSplitPanel.getItems().size() > 0) {
            if (!consolePanelIsDisplay) {
                mainSplitPanel.getItems().add(consoleSplitPanel);
                mainSplitPanel.setDividerPosition(0, 0.75);
                consolePanelIsDisplay = true;
            }
        } else {
            if (consolePanelIsDisplay) {
                mainSplitPanel.setDividerPosition(0, 1);
                mainSplitPanel.getItems().remove(consoleSplitPanel);
                consolePanelIsDisplay = false;
            }
        }
    }

    public void toggleMiniJajaConsolePanel() {
        if (consoleMiniJajaPanelIsDisplay) {
            consoleSplitPanel.getItems().remove(consoleMiniJajaPanel);
            consoleSplitPanel.setDividerPosition(0, 1);
        } else {
            consoleSplitPanel.getItems().add(consoleMiniJajaPanel);
            consoleSplitPanel.setDividerPosition(0, 0.45);
        }
        consoleMiniJajaPanelIsDisplay = !consoleMiniJajaPanelIsDisplay;
        toogleConsoleSplitIfAlreadyOpen();
        consoleMiniJajaToogleMenuItemIcon();

    }
    public void toggleJajaCodeConsolePanel() {
        if (consoleJajaCodePanelIsDisplay) {
            consoleSplitPanel.getItems().remove(consoleJajaCodePanel);
            consoleSplitPanel.setDividerPosition(0, 1);
        } else {
            consoleSplitPanel.getItems().add(consoleJajaCodePanel);
            consoleSplitPanel.setDividerPosition(0, 0.45);
        }
        consoleJajaCodePanelIsDisplay = !consoleJajaCodePanelIsDisplay;
        toogleConsoleSplitIfAlreadyOpen();
        consoleJajaCodeToogleMenuItemIcon();
    }

    private int getNumberVisiblePanel(SplitPane splitPane) {
        int n = 0;
        for (Node node: splitPane.getItems()) {
            if (node.isVisible()) {
                n++;
            }
        }
        return n;
    }

    private void setLengthOfPanelAuto() {
        int nbVisiblePane = getNumberVisiblePanel(secondSplitPanel);
        if (nbVisiblePane == 1) {
            secondSplitPanel.setDividerPositions(1.0);
        } else if (nbVisiblePane == 2) {
            secondSplitPanel.setDividerPositions(0.75);
        } else {
            secondSplitPanel.setDividerPositions(0.5, 0.75);
        }
    }

    /**
     * About Actions
     */

    @FXML
    protected void about() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Compilator Project - Group 2 TPA");
        alert.setContentText(
            "GRAPPE ROBIN\n" +
            "GROSJEAN THIBAULT\n" +
            "HAKKAR TAYEB\n" +
            "HOFER ERWAN\n" +
            "JACQUIN ELEA\n" +
            "LAMY CLEMENT\n" +
            "PERRIN JEREMY\n"
        );
        alert.show();
    }


    /**
     * Compilator Actions
     */

    @FXML
    public void compile() throws VisitorException, ParseException {
        if (!consoleJajaCodePanelIsDisplay) {
            toggleJajaCodeConsolePanel();
        }
        cleanJajaCodeOutput();
        // Run Compile
        if(!isMiniJajaAreaEmpty()){
            cleanJajaCodeOutput();
            writeJajaCodeOutputConsole("************ Compilation start ************");
            jajaCodeTextArea.clear();
            try {
                Reader reader = new StringReader(miniJajaTextArea.getText());
                MiniJaja.ReInit(reader);
                root = MiniJaja.Start();

                typeChecker = new TypeChecker(root,new SymbolTable());
                try {
                    typeChecker.typeCheck();
                    typeChecker.astToStringScopedRoot();
                } catch (VisitorException e) {
                    jajaCodeOutput.addError(e.getMessage(), e.getL(), e.getC());
                    writeJajaCodeOutputConsole(jajaCodeOutput.showAllLogs());
                    highlightErrors(jajaCodeOutput);
                    writeJajaCodeOutputConsole("************ Compilation end ************");
                    return;
                }

                compiler = new Compiler(root);
                List<fr.ufrst.m1info.projetcomp.m1comp2.ast.jjc.Node> instructions = compiler.compile();
                if(!instructions.isEmpty()){
                    interpretationInstructions = instructions;
                }
                //jajaCodeTextArea.appendText(CompilerToString.instrToString(instructions));
            }
            catch(ParseException e){
                jajaCodeOutput.addError(e.getMessage(), e.getL(), e.getC());
                writeJajaCodeOutputConsole(jajaCodeOutput.showAllLogs());
                highlightErrors(jajaCodeOutput);
                writeJajaCodeOutputConsole("************ Compilation end ************");
                return;
            }catch (TokenMgrError e){
                jajaCodeOutput.addError(e.getMessage(),0,0);
                writeJajaCodeOutputConsole(jajaCodeOutput.showAllLogs());
                highlightErrors(jajaCodeOutput);
                writeJajaCodeOutputConsole("************ Compilation end ************");
                return;
            }
            String jajaCodeText = CompilerToString.instrToString(interpretationInstructions);
            // Removing the \n at the end
            jajaCodeText = jajaCodeText.substring(0, jajaCodeText.length()-1);
            writeJavaCodeLine(jajaCodeText);
            writeJajaCodeOutputConsole("************ Compilation end ************");
        }
    }

    @FXML
    public void debugMiniJaja() throws VisitorException, ParseException {
        // Run debug
        if (!consoleMiniJajaPanelIsDisplay) {
            toggleMiniJajaConsolePanel();
        }
        cleanMiniJajaOutput();
        toogleDebugButton();

        debugger = new Debugger();
        debugger.addObserver(this);
        debugger.clearBreakPoint();
        debugger.addBreakPoints(breakPointsMiniJaja);
        symbolTable = new SymbolTable();
        memory = new Memory(symbolTable);

        Reader reader = new StringReader(miniJajaTextArea.getText());
        MiniJaja.ReInit(reader);
        root = MiniJaja.Start();


        typeChecker = new TypeChecker(root,new SymbolTable());
        try {
            typeChecker.typeCheck();
        } catch (VisitorException e) {
            miniJajaOutput.addError(e.getMessage(), e.getL(), e.getC());
            writeMiniJajaOutputConsole(miniJajaOutput.showAllLogs());
            writeMiniJajaOutputConsole("************ Compilation end ************");
            return;
        }

        interpreterMjj = new InterpreterMjj(root,memory);
        interpreterMjj.setDebugger(debugger);
        debugger.setInterpreter(interpreterMjj);
        debugger.debugMJJ();

        writeMiniJajaOutputConsole("************ Debug ************");
    }

    @FXML
    public void debugJajaCode() throws VisitorException {
        // Run debug
        if(interpretationInstructions == null || interpretationInstructions.isEmpty()){
            return;
        }
        if (!consoleJajaCodePanelIsDisplay) {
            toggleJajaCodeConsolePanel();
        }
        cleanJajaCodeOutput();
        toogleDebugButton();

        debugger = new Debugger();
        debugger.addObserver(this);
        debugger.addBreakPoints(breakPointsJajaCode);
        symbolTable = new SymbolTable();
        memory = new Memory(symbolTable);
        compiler = new Compiler(root);
        List<fr.ufrst.m1info.projetcomp.m1comp2.ast.jjc.Node> instructions = compiler.compile();
        if(!instructions.isEmpty()){
            interpretationInstructions = instructions;
        }
        interpreterJjc = new InterpreterJjc(interpretationInstructions,memory);
        interpreterJjc.setDebugger(debugger);
        debugger.setInterpreter(interpreterJjc);
        debugger.debugJJC();

        writeJajaCodeOutputConsole("************ Debug ************");
    }

    private void toogleDebugButton() {
        toogleDebugOption = !toogleDebugOption;
        showHideButtonDebug();
    }

    public void showHideButtonDebug() {
        if (toogleDebugOption) {
            buttonBar.getButtons().remove(runBtn);
            buttonBar.getButtons().remove(stopBtn);
            buttonBar.getButtons().add(debugStepIntoBtn);
            buttonBar.getButtons().add(debugStepOverBtn);
            buttonBar.getButtons().add(runBtn);
            buttonBar.getButtons().add(stopBtn);
        } else {
            buttonBar.getButtons().remove(debugStepIntoBtn);
            buttonBar.getButtons().remove(debugStepOverBtn);
        }
    }

    @FXML
    public void debugStepInto() {
        if(debugger.getThread() == null){
            writeJajaCodeOutputConsole("************ Debug end ************");
            cleanHighlightDebugLineJajaCode();
            toogleDebugButton();
        }
        List<String> stack = new ArrayList<>();
        for(Quad quad : memory.getStack().getValues()){
            stack.add(quad.toString());
        }
        writeMemoryLines(stack);
        debugger.next();
    }

    @FXML
    public void debugStepOver() {
        if(debugger.getThread() == null){
            writeJajaCodeOutputConsole("************ Debug end ************");
            cleanHighlightDebugLineJajaCode();
            toogleDebugButton();
        }
        List<String> stack = new ArrayList<>();
        for(Quad quad : memory.getStack().getValues()){
            stack.add(quad.toString());
        }
        writeMemoryLines(stack);
        debugger.next();
    }

    @Override
    public void notifyChange(List<String> stack){
        Platform.runLater(() -> {
            writeMemoryLines(stack);
        });
    }



    @Override
    public void printInJajaCodeOutput(String s) {
        Platform.runLater(() -> writeJajaCodeOutputConsole(s));
    }

    @Override
    public void printInMiniJajaCodeOutput(String s) {
        Platform.runLater(() -> writeMiniJajaOutputConsole(s));
    }

    @Override
    public void hideButtons() {
        Platform.runLater(() -> toogleDebugButton());
    }

    /**
     * Compilator Actions
     */
    private boolean isMiniJajaAreaEmpty(){
        return miniJajaTextArea.getText().isEmpty();
    }

    @FXML
    public void runMiniJaja() {
        // Run
        if (!consoleMiniJajaPanelIsDisplay) {
            toggleMiniJajaConsolePanel();
        }
        debugger = null;
        cleanMiniJajaOutput();
        writeMiniJajaOutputConsole("************ Run MiniJaja start ************");
        try {
            Reader reader = new StringReader(miniJajaTextArea.getText());
            MiniJaja.ReInit(reader);
            root = MiniJaja.Start();

            typeChecker = new TypeChecker(root,new SymbolTable());
            try {
                typeChecker.typeCheck();
            } catch (VisitorException e) {
                miniJajaOutput.addError(e.getMessage(), e.getL(), e.getC());
                writeMiniJajaOutputConsole(miniJajaOutput.showAllLogs());
                highlightErrors(miniJajaOutput);
                writeMiniJajaOutputConsole("************ Run MiniJaja end ************");
                return;
            }
        }
        catch(ParseException e){
            miniJajaOutput.addError(e.getMessage(), e.getL(), e.getC());
            writeMiniJajaOutputConsole(miniJajaOutput.showAllLogs());
            highlightErrors(miniJajaOutput);
            writeMiniJajaOutputConsole("************ Run MiniJaja end ************");
            return;
        }

        symbolTable = new SymbolTable();
        memory = new Memory(symbolTable);
        interpreterMjj = new InterpreterMjj(root, memory);
        try {
            writeMiniJajaOutputConsole(interpreterMjj.interpret().replace("\"", ""));
        } catch (VisitorException e) {
            miniJajaOutput.addError(e.getMessage(), e.getL(), e.getC());
            writeMiniJajaOutputConsole(miniJajaOutput.showAllLogs());
            highlightErrors(miniJajaOutput);
            writeMiniJajaOutputConsole("************ Run MiniJaja end ************");
            return;
        }
        writeMiniJajaOutputConsole("************ Run MiniJaja end ************");
    }

    public void highlightErrors(OutputConsole output) {
        /*for (OutputConsole.Error err : output.getErrors()) {
            highlightMjj(err.getLine());
        }*/
    }

    @FXML
    public void runJajaCode() {
        // Run
        if (!consoleJajaCodePanelIsDisplay) {
            toggleJajaCodeConsolePanel();
        }
        if(!isMiniJajaAreaEmpty() && interpretationInstructions!=null && !interpretationInstructions.isEmpty()){
            cleanJajaCodeOutput();
            writeJajaCodeOutputConsole("************ Run JajaCode start ************");
            debugger = null;
            symbolTable = new SymbolTable();
            memory = new Memory(symbolTable);
            interpreterJjc = new InterpreterJjc(interpretationInstructions, memory);
            try {
                writeJajaCodeOutputConsole(interpreterJjc.interpret().replace("\"", ""));
            } catch (VisitorException e) {
                jajaCodeOutput.addError(e.getMessage(), e.getL(), e.getC());
                writeJajaCodeOutputConsole(jajaCodeOutput.showAllLogs());
                highlightErrors(jajaCodeOutput);
                writeJajaCodeOutputConsole("************ Run JajaCode end ************");
                return;
            }
            writeJajaCodeOutputConsole("************ Run JajaCode end ************");
        }
    }

    @FXML
    public void stop() {
        // Stop
        writeMiniJajaOutputConsole("************ Stop ************");
        writeMiniJajaOutputConsole("************ Stop ************");
    }

    public Set<Integer> getBreakPointsMiniJaja() {
        return this.breakPointsMiniJaja;
    }

    public Set<Integer> getBreakPointsJajaCode() {
        return this.breakPointsJajaCode;
    }

    public void clearBreakPoints() {
        this.breakPointsMiniJaja.clear();
        this.breakPointsJajaCode.clear();
    }

    public void cleanAllOutputConsole() {
        cleanMiniJajaOutput();
        cleanJajaCodeOutput();
    }

    @FXML
    public void cleanMiniJajaOutput() {
        miniJajaOutputArea.clear();
        miniJajaOutput.removeAllLogs();
    }
    @FXML
    public void cleanJajaCodeOutput() {
        jajaCodeOutputArea.clear();
        jajaCodeOutput.removeAllLogs();
    }

    @FXML
    public void cleanJajaCode() {
        jajaCodeTextArea.clear();
    }

    @FXML
    public void cleanMemory() {
        memoryGridPane.getChildren().clear();
    }

    @FXML
    public void writeMiniJajaOutputConsole(String text) {
        miniJajaOutputArea.appendText(text + "\n");
    }

    @FXML
    public void writeJajaCodeOutputConsole(String text) {
        jajaCodeOutputArea.appendText(text + "\n");
    }

    @FXML
    public void writeJavaCodeLine(String text) {
        jajaCodeTextArea.replaceText(text);
    }

    public void writeJavaCodeLines(List<String> lines) {
        cleanJajaCode();
        for (String line: lines) {
            writeJavaCodeLine(line);
        }
    }


    public void writeMemoryLines(List<String> stack) {
        cleanMemory();

        for (String row : stack) {
            Label label = new Label();
            label.setPrefHeight(HEIGHT_ROW_STACK);
            label.setMaxWidth(Double.POSITIVE_INFINITY);
            label.setStyle("-fx-border-color: lightgray;-fx-alignment: center;");
            label.setText(row);

            memoryGridPane.getChildren().add(label);
        }
    }

    public void toogleDarkMode() {
        if (darkmode) {
            stage.getScene().getStylesheets().remove(IDEApplication.class.getResource("styles/dark-mode.css").toExternalForm());
        } else {
            stage.getScene().getStylesheets().add(IDEApplication.class.getResource("styles/dark-mode.css").toExternalForm());
        }
        darkmode = !darkmode;
        updateIconsMenuItem();
    }

    public void toggleFileOptionIfOpen() {
        closeFileBtn.setDisable(!fileOpened);
        saveFileBtn.setDisable(!fileOpened);
        saveFileAsBtn.setDisable(!fileOpened);
    }

    public void scrollToLineMiniJaja(int line) {
        cleanHighlightDebugLineMiniJaja();
        miniJajaTextArea.showParagraphAtCenter(line - 1);
        miniJajaTextArea.moveTo(line - 1, 0);
        miniJajaTextArea.setStyle(line-1, Collections.singleton("error-line"));
    }

    public void scrollToLineJajaCode(int line) {
        cleanHighlightDebugLineJajaCode();
        jajaCodeTextArea.showParagraphAtCenter(line - 1);
        jajaCodeTextArea.moveTo(line - 1, 0);
        jajaCodeTextArea.setStyle(line-1, Collections.singleton("debug-line"));
    }

    private void cleanHighlightDebugLineMiniJaja() {
        for (int i = 0; i < miniJajaTextArea.getParagraphs().size(); i++) {
            miniJajaTextArea.setStyle(i, Collections.singleton("line"));
        }
    }

    private void cleanHighlightDebugLineJajaCode() {
        for (int i = 0; i < jajaCodeTextArea.getParagraphs().size(); i++) {
            jajaCodeTextArea.setStyle(i, Collections.singleton("line"));
        }
    }

    @Override
    public void highlightJjc(int i) {
        Platform.runLater(() -> {
            scrollToLineJajaCode(i-1);
        });
    }

    @Override
    public void highlightMjj(int i) {
        Platform.runLater(() -> {
            scrollToLineMiniJaja(i-1);
        });
    }
}