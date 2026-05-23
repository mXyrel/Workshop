package com.workshop.bbs.controller;

import com.workshop.bbs.dao.BookDAO;
import com.workshop.bbs.dao.BorrowRecordDAO;
import com.workshop.bbs.dao.StudentDAO;
import com.workshop.bbs.model.Book;
import com.workshop.bbs.model.BorrowRecord;
import com.workshop.bbs.model.Student;
import com.workshop.bbs.util.Session;
import com.workshop.bbs.view.SceneManager;
import com.workshop.bbs.view.Styles;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * BORROW RECORDS SCREEN
 * Scene 3 of 3 — manage borrowing transactions.
 */
public class RecordsController {

    private final BorderPane root = new BorderPane();
    private final BorrowRecordDAO brDAO   = new BorrowRecordDAO();
    private final BookDAO bookDAO         = new BookDAO();
    private final StudentDAO studentDAO   = new StudentDAO();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private TableView<BorrowRecord> table;
    private ObservableList<BorrowRecord> recordList = FXCollections.observableArrayList();
    private ComboBox<String> filterBox;
    private Label statusLabel;

    public RecordsController() {
        buildView();
        loadRecords();
    }

    public BorderPane getView() { return root; }

    // ── Header ───────────────────────────────────────────────────────
    private HBox buildHeader() {
        HBox header = new HBox(12);
        header.setStyle(Styles.HEADER);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title    = new Label("PUP Book Borrowing System");
        title.setStyle(Styles.HEADER_TITLE);
        Label subtitle = new Label("Workshop  ·  Might Malinay, BSIT 2-2");
        subtitle.setStyle(Styles.HEADER_SUBTITLE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button dashBtn    = new Button("Dashboard");
        Button recordsBtn = new Button("Borrow Records");
        Button logoutBtn  = new Button("Logout");

        dashBtn.setStyle(Styles.NAV_LINK);
        recordsBtn.setStyle(Styles.NAV_LINK_ACTIVE);
        logoutBtn.setStyle(Styles.NAV_LINK);

        dashBtn.setOnAction(e -> SceneManager.switchTo("dashboard"));
        logoutBtn.setOnAction(e -> { Session.clear(); SceneManager.switchTo("login"); });

        String user = Session.isLoggedIn() ? Session.getUser().getFullName() : "User";
        Label userLabel = new Label("👤 " + user);
        userLabel.setStyle(Styles.HEADER_SUBTITLE);

        header.getChildren().addAll(title, spacer, subtitle, dashBtn, recordsBtn, logoutBtn, userLabel);
        return header;
    }

    private void buildView() {
        root.setStyle(Styles.ROOT);
        root.setTop(buildHeader());
        root.setCenter(buildContent());
    }

    // ── Main content ─────────────────────────────────────────────────
    private VBox buildContent() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(24));
        content.setStyle("-fx-background-color: #F9F9F9;");
        VBox.setVgrow(content, Priority.ALWAYS);

        content.getChildren().addAll(buildToolbar(), buildTable());
        return content;
    }

    // ── Toolbar ──────────────────────────────────────────────────────
    private HBox buildToolbar() {
        HBox bar = new HBox(10);
        bar.setAlignment(Pos.CENTER_LEFT);

        Label pageTitle = new Label("Borrow Records");
        pageTitle.setStyle(Styles.SECTION_TITLE);

        filterBox = new ComboBox<>(FXCollections.observableArrayList("All", "borrowed", "returned", "overdue"));
        filterBox.setValue("All");
        filterBox.setStyle(Styles.INPUT);
        filterBox.setOnAction(e -> loadRecords());

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle(Styles.BTN_SECONDARY);
        refreshBtn.setOnAction(e -> loadRecords());

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        statusLabel = new Label("");
        statusLabel.setStyle(Styles.LABEL_MUTED);

        Button addBtn = new Button("+ New Borrow");
        addBtn.setStyle(Styles.BTN_PRIMARY);
        addBtn.setOnMouseEntered(e -> addBtn.setStyle(Styles.BTN_PRIMARY_HOVER));
        addBtn.setOnMouseExited(e  -> addBtn.setStyle(Styles.BTN_PRIMARY));
        addBtn.setOnAction(e -> showNewBorrowDialog());

        bar.getChildren().addAll(pageTitle, filterBox, refreshBtn, sp, statusLabel, addBtn);
        return bar;
    }

    // ── Table ────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private TableView<BorrowRecord> buildTable() {
        table = new TableView<>();
        table.setStyle(Styles.TABLE);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(recordList);
        table.setPlaceholder(new Label("No records found."));
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<BorrowRecord, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getId()).asObject());
        idCol.setMaxWidth(55);

        TableColumn<BorrowRecord, String> studentCol = new TableColumn<>("Student");
        studentCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getStudentNumber() + " — " + d.getValue().getStudentName()));

        TableColumn<BorrowRecord, String> bookCol = new TableColumn<>("Book");
        bookCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getBookTitle()));

        TableColumn<BorrowRecord, String> borrowDateCol = new TableColumn<>("Borrow Date");
        borrowDateCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getBorrowDate() != null ? d.getValue().getBorrowDate().format(FMT) : "—"));

        TableColumn<BorrowRecord, String> dueDateCol = new TableColumn<>("Due Date");
        dueDateCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getDueDate() != null ? d.getValue().getDueDate().format(FMT) : "—"));

        TableColumn<BorrowRecord, String> returnDateCol = new TableColumn<>("Return Date");
        returnDateCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getReturnDate() != null ? d.getValue().getReturnDate().format(FMT) : "—"));

        TableColumn<BorrowRecord, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle(switch (item) {
                    case "borrowed"  -> "-fx-text-fill: #E65100; -fx-font-weight: bold;";
                    case "returned"  -> "-fx-text-fill: #2E7D32; -fx-font-weight: bold;";
                    case "overdue"   -> "-fx-text-fill: #C62828; -fx-font-weight: bold;";
                    default          -> "";
                });
            }
        });

        TableColumn<BorrowRecord, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setMinWidth(180);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button returnBtn = new Button("Mark Returned");
            private final Button deleteBtn = new Button("Delete");
            private final HBox box = new HBox(6, returnBtn, deleteBtn);

            {
                returnBtn.setStyle(Styles.BTN_SECONDARY);
                deleteBtn.setStyle(Styles.BTN_DANGER);
                box.setAlignment(Pos.CENTER);

                returnBtn.setOnAction(e -> {
                    BorrowRecord r = getTableView().getItems().get(getIndex());
                    handleReturn(r);
                });
                deleteBtn.setOnAction(e -> {
                    BorrowRecord r = getTableView().getItems().get(getIndex());
                    handleDelete(r);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                BorrowRecord r = getTableView().getItems().get(getIndex());
                returnBtn.setDisable("returned".equals(r.getStatus()));
                setGraphic(box);
            }
        });

        table.getColumns().addAll(idCol, studentCol, bookCol, borrowDateCol, dueDateCol, returnDateCol, statusCol, actionsCol);
        return table;
    }

    // ── Data ─────────────────────────────────────────────────────────
    private void loadRecords() {
        String filter = filterBox != null ? filterBox.getValue() : "All";
        Thread t = new Thread(() -> {
            try {
                brDAO.refreshOverdueStatuses();
                List<BorrowRecord> records = "All".equals(filter)
                        ? brDAO.findAll()
                        : brDAO.findByStatus(filter);
                javafx.application.Platform.runLater(() -> {
                    recordList.setAll(records);
                    if (statusLabel != null)
                        statusLabel.setText(records.size() + " record(s)");
                });
            } catch (Exception ex) {
                showError("Failed to load records: " + ex.getMessage());
            }
        });
        t.setDaemon(true);
        t.start();
    }

    // ── New Borrow Dialog ────────────────────────────────────────────
    private void showNewBorrowDialog() {
        Dialog<BorrowRecord> dialog = new Dialog<>();
        dialog.setTitle("New Borrow Transaction");
        dialog.setHeaderText("Record a book borrowing");

        ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 30, 10, 10));

        // Student picker
        ComboBox<Student> studentBox = new ComboBox<>();
        studentBox.setStyle(Styles.INPUT);
        studentBox.setPrefWidth(300);

        // Book picker
        ComboBox<Book> bookBox = new ComboBox<>();
        bookBox.setStyle(Styles.INPUT);
        bookBox.setPrefWidth(300);

        // Due date
        DatePicker duePicker = new DatePicker(LocalDate.now().plusDays(7));
        duePicker.setStyle(Styles.INPUT);

        Label errorLbl = new Label("");
        errorLbl.setStyle(Styles.ERROR_LABEL);

        grid.add(new Label("Student"),  0, 0); grid.add(studentBox, 1, 0);
        grid.add(new Label("Book"),     0, 1); grid.add(bookBox,    1, 1);
        grid.add(new Label("Due Date"), 0, 2); grid.add(duePicker,  1, 2);
        grid.add(errorLbl,              1, 3);

        dialog.getDialogPane().setContent(grid);

        // Load students & books asynchronously
        Thread loadThread = new Thread(() -> {
            try {
                List<Student> students = studentDAO.findAll();
                List<Book>    books    = bookDAO.findAll().stream()
                        .filter(Book::isAvailable).toList();
                javafx.application.Platform.runLater(() -> {
                    studentBox.setItems(FXCollections.observableArrayList(students));
                    bookBox.setItems(FXCollections.observableArrayList(books));
                });
            } catch (Exception ex) {
                showError("Failed to load data: " + ex.getMessage());
            }
        });
        loadThread.setDaemon(true);
        loadThread.start();

        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                if (studentBox.getValue() == null) { errorLbl.setText("Select a student."); return null; }
                if (bookBox.getValue()    == null) { errorLbl.setText("Select a book.");    return null; }
                BorrowRecord r = new BorrowRecord();
                r.setStudentId(studentBox.getValue().getId());
                r.setBookId(bookBox.getValue().getId());
                r.setBorrowDate(LocalDate.now());
                r.setDueDate(duePicker.getValue());
                r.setStatus("borrowed");
                return r;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(r -> {
            Thread t = new Thread(() -> {
                try {
                    brDAO.create(r);
                    javafx.application.Platform.runLater(this::loadRecords);
                } catch (Exception ex) {
                    showError("Failed to save: " + ex.getMessage());
                }
            });
            t.setDaemon(true);
            t.start();
        });
    }

    private void handleReturn(BorrowRecord r) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Mark this record as returned?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                Thread t = new Thread(() -> {
                    try {
                        brDAO.markReturned(r.getId());
                        javafx.application.Platform.runLater(this::loadRecords);
                    } catch (Exception ex) {
                        showError("Return failed: " + ex.getMessage());
                    }
                });
                t.setDaemon(true);
                t.start();
            }
        });
    }

    private void handleDelete(BorrowRecord r) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete this record? This cannot be undone.", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                Thread t = new Thread(() -> {
                    try {
                        brDAO.delete(r.getId());
                        javafx.application.Platform.runLater(this::loadRecords);
                    } catch (Exception ex) {
                        showError("Delete failed: " + ex.getMessage());
                    }
                });
                t.setDaemon(true);
                t.start();
            }
        });
    }

    private void showError(String msg) {
        javafx.application.Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
            a.setHeaderText(null);
            a.showAndWait();
        });
    }
}
