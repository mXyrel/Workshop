package com.workshop.bbs.controller;

import com.workshop.bbs.dao.BookDAO;
import com.workshop.bbs.dao.BorrowRecordDAO;
import com.workshop.bbs.dao.StudentDAO;
import com.workshop.bbs.model.Book;
import com.workshop.bbs.util.Session;
import com.workshop.bbs.view.SceneManager;
import com.workshop.bbs.view.Styles;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;

import java.util.List;

/**
 * DASHBOARD SCREEN
 * Scene 2 of 3 — shows stats, book list, add/edit/delete books.
 */
public class DashboardController {

    private final BorderPane root = new BorderPane();
    private final BookDAO bookDAO         = new BookDAO();
    private final BorrowRecordDAO brDAO   = new BorrowRecordDAO();
    private final StudentDAO studentDAO   = new StudentDAO();

    private TableView<Book> bookTable;
    private ObservableList<Book> bookList = FXCollections.observableArrayList();
    private TextField searchField;

    private Label statBooks, statAvailable, statBorrowed, statStudents;

    public DashboardController() {
        buildView();
        loadData();
    }

    public BorderPane getView() { return root; }

    private void buildView() {
        root.setStyle(Styles.ROOT);
        root.setTop(buildHeader());
        root.setCenter(buildContent());
    }

    // ── Header ───────────────────────────────────────────────────────
    private HBox buildHeader() {
        HBox header = new HBox(12);
        header.setStyle(Styles.HEADER);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("PUP Book Borrowing System");
        title.setStyle(Styles.HEADER_TITLE);

        Label subtitle = new Label("Workshop  ·  Might Malinay, BSIT 2-2");
        subtitle.setStyle(Styles.HEADER_SUBTITLE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Nav buttons
        Button dashBtn    = new Button("Dashboard");
        Button recordsBtn = new Button("Borrow Records");
        Button logoutBtn  = new Button("Logout");

        dashBtn.setStyle(Styles.NAV_LINK_ACTIVE);
        recordsBtn.setStyle(Styles.NAV_LINK);
        logoutBtn.setStyle(Styles.NAV_LINK);

        recordsBtn.setOnAction(e -> SceneManager.switchTo("records"));
        logoutBtn.setOnAction(e -> {
            Session.clear();
            SceneManager.switchTo("login");
        });

        String user = Session.isLoggedIn() ? Session.getUser().getFullName() : "User";
        Label userLabel = new Label("👤 " + user);
        userLabel.setStyle(Styles.HEADER_SUBTITLE);

        header.getChildren().addAll(title, spacer, subtitle, dashBtn, recordsBtn, logoutBtn, userLabel);
        return header;
    }

    // ── Main content ─────────────────────────────────────────────────
    private VBox buildContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(24));
        content.setStyle("-fx-background-color: #F9F9F9;");

        content.getChildren().addAll(
                buildStatsRow(),
                buildBooksPanel()
        );
        return content;
    }

    // ── Stat cards ───────────────────────────────────────────────────
    private HBox buildStatsRow() {
        statBooks     = new Label("—");
        statAvailable = new Label("—");
        statBorrowed  = new Label("—");
        statStudents  = new Label("—");

        HBox row = new HBox(16);
        row.getChildren().addAll(
                statCard("Total Books",      statBooks,     "#1565C0"),
                statCard("Available Copies", statAvailable, "#2E7D32"),
                statCard("Currently Borrowed",statBorrowed, "#E65100"),
                statCard("Students",         statStudents,  "#4A148C")
        );
        return row;
    }

    private VBox statCard(String label, Label value, String colour) {
        VBox card = new VBox(4);
        card.setStyle(Styles.CARD + " -fx-border-left-width: 4; -fx-border-left-color: " + colour + ";");
        card.setPadding(new Insets(14, 18, 14, 18));
        HBox.setHgrow(card, Priority.ALWAYS);

        Label lbl = new Label(label);
        lbl.setStyle(Styles.LABEL_MUTED);
        value.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + colour + ";");
        card.getChildren().addAll(value, lbl);
        return card;
    }

    // ── Books panel ──────────────────────────────────────────────────
    private VBox buildBooksPanel() {
        VBox panel = new VBox(12);
        panel.setStyle(Styles.CARD);
        VBox.setVgrow(panel, Priority.ALWAYS);

        // Toolbar
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        Label panelTitle = new Label("Books");
        panelTitle.setStyle(Styles.SECTION_TITLE);

        searchField = new TextField();
        searchField.setPromptText("Search title or author…");
        searchField.setStyle(Styles.INPUT);
        searchField.setPrefWidth(220);
        searchField.setOnAction(e -> handleSearch());

        Button searchBtn = new Button("Search");
        searchBtn.setStyle(Styles.BTN_SECONDARY);
        searchBtn.setOnAction(e -> handleSearch());

        Button clearBtn = new Button("Clear");
        clearBtn.setStyle(Styles.BTN_SECONDARY);
        clearBtn.setOnAction(e -> { searchField.clear(); loadBooks(); });

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button addBtn = new Button("+ Add Book");
        addBtn.setStyle(Styles.BTN_PRIMARY);
        addBtn.setOnMouseEntered(e -> addBtn.setStyle(Styles.BTN_PRIMARY_HOVER));
        addBtn.setOnMouseExited(e  -> addBtn.setStyle(Styles.BTN_PRIMARY));
        addBtn.setOnAction(e -> showBookDialog(null));

        toolbar.getChildren().addAll(panelTitle, searchField, searchBtn, clearBtn, sp, addBtn);

        // Table
        bookTable = buildBookTable();
        bookTable.setItems(bookList);
        VBox.setVgrow(bookTable, Priority.ALWAYS);

        panel.getChildren().addAll(toolbar, bookTable);
        return panel;
    }

    @SuppressWarnings("unchecked")
    private TableView<Book> buildBookTable() {
        TableView<Book> table = new TableView<>();
        table.setStyle(Styles.TABLE);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No books found."));

        TableColumn<Book, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setMaxWidth(50);

        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));

        TableColumn<Book, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Book, String> isbnCol = new TableColumn<>("ISBN");
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));

        TableColumn<Book, Integer> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalCopies"));
        totalCol.setMaxWidth(60);

        TableColumn<Book, String> availCol = new TableColumn<>("Available");
        availCol.setCellValueFactory(d -> {
            int avail = d.getValue().getAvailableCopies();
            return new SimpleStringProperty(avail > 0 ? String.valueOf(avail) : "—");
        });
        availCol.setMaxWidth(80);

        // Actions column
        TableColumn<Book, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setMinWidth(160);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn   = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox box         = new HBox(6, editBtn, deleteBtn);

            {
                editBtn.setStyle(Styles.BTN_SECONDARY);
                deleteBtn.setStyle(Styles.BTN_DANGER);
                box.setAlignment(Pos.CENTER);

                editBtn.setOnAction(e -> {
                    Book b = getTableView().getItems().get(getIndex());
                    showBookDialog(b);
                });
                deleteBtn.setOnAction(e -> {
                    Book b = getTableView().getItems().get(getIndex());
                    handleDeleteBook(b);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        table.getColumns().addAll(idCol, titleCol, authorCol, categoryCol, isbnCol, totalCol, availCol, actionsCol);
        return table;
    }

    // ── Data loading ─────────────────────────────────────────────────
    private void loadData() {
        loadBooks();
        loadStats();
    }

    private void loadBooks() {
        Thread t = new Thread(() -> {
            try {
                List<Book> books = bookDAO.findAll();
                javafx.application.Platform.runLater(() -> {
                    bookList.setAll(books);
                });
            } catch (Exception ex) {
                showError("Failed to load books: " + ex.getMessage());
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void handleSearch() {
        String kw = searchField.getText().trim();
        if (kw.isEmpty()) { loadBooks(); return; }
        Thread t = new Thread(() -> {
            try {
                List<Book> books = bookDAO.search(kw);
                javafx.application.Platform.runLater(() -> bookList.setAll(books));
            } catch (Exception ex) {
                showError("Search error: " + ex.getMessage());
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void loadStats() {
        Thread t = new Thread(() -> {
            try {
                int books     = bookDAO.countAll();
                int available = bookDAO.countAvailable();
                int borrowed  = brDAO.countByStatus("borrowed");
                int students  = studentDAO.countAll();
                javafx.application.Platform.runLater(() -> {
                    statBooks.setText(String.valueOf(books));
                    statAvailable.setText(String.valueOf(available));
                    statBorrowed.setText(String.valueOf(borrowed));
                    statStudents.setText(String.valueOf(students));
                });
            } catch (Exception ignored) {}
        });
        t.setDaemon(true);
        t.start();
    }

    // ── Book dialog (Add / Edit) ──────────────────────────────────────
    private void showBookDialog(Book existing) {
        boolean isEdit = existing != null;
        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Edit Book" : "Add Book");
        dialog.setHeaderText(null);

        ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleF    = styledField(isEdit ? existing.getTitle() : "");
        TextField authorF   = styledField(isEdit ? existing.getAuthor() : "");
        TextField isbnF     = styledField(isEdit ? existing.getIsbn() : "");
        TextField categoryF = styledField(isEdit ? existing.getCategory() : "");
        TextField totalF    = styledField(isEdit ? String.valueOf(existing.getTotalCopies()) : "1");
        TextField availF    = styledField(isEdit ? String.valueOf(existing.getAvailableCopies()) : "1");

        grid.add(new Label("Title"),             0, 0); grid.add(titleF,    1, 0);
        grid.add(new Label("Author"),            0, 1); grid.add(authorF,   1, 1);
        grid.add(new Label("ISBN"),              0, 2); grid.add(isbnF,     1, 2);
        grid.add(new Label("Category"),          0, 3); grid.add(categoryF, 1, 3);
        grid.add(new Label("Total Copies"),      0, 4); grid.add(totalF,    1, 4);
        if (isEdit) {
            grid.add(new Label("Available Copies"), 0, 5);
            grid.add(availF, 1, 5);
        }

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                Book b = isEdit ? existing : new Book();
                b.setTitle(titleF.getText().trim());
                b.setAuthor(authorF.getText().trim());
                b.setIsbn(isbnF.getText().trim());
                b.setCategory(categoryF.getText().trim());
                try {
                    b.setTotalCopies(Integer.parseInt(totalF.getText().trim()));
                    b.setAvailableCopies(isEdit ? Integer.parseInt(availF.getText().trim()) : b.getTotalCopies());
                } catch (NumberFormatException e) {
                    b.setTotalCopies(1);
                    b.setAvailableCopies(1);
                }
                return b;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(b -> {
            Thread t = new Thread(() -> {
                try {
                    if (isEdit) bookDAO.update(b);
                    else bookDAO.create(b);
                    javafx.application.Platform.runLater(() -> { loadData(); });
                } catch (Exception ex) {
                    showError("Save failed: " + ex.getMessage());
                }
            });
            t.setDaemon(true);
            t.start();
        });
    }

    private TextField styledField(String value) {
        TextField f = new TextField(value);
        f.setStyle(Styles.INPUT);
        f.setPrefWidth(220);
        return f;
    }

    private void handleDeleteBook(Book b) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete \"" + b.getTitle() + "\"? This cannot be undone.",
                ButtonType.YES, ButtonType.NO);
        alert.setHeaderText(null);
        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                Thread t = new Thread(() -> {
                    try {
                        bookDAO.delete(b.getId());
                        javafx.application.Platform.runLater(this::loadData);
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
