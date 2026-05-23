package com.workshop.bbs.view;

/**
 * Centralised style constants for the minimalist UI.
 * Palette: white background, dark text, subtle grey borders, maroon accent (PUP colour).
 */
public final class Styles {

    // Colours
    public static final String BG          = "#FFFFFF";
    public static final String BG_LIGHT    = "#F5F5F5";
    public static final String ACCENT      = "#800000"; // PUP maroon
    public static final String ACCENT_DARK = "#600000";
    public static final String TEXT        = "#212121";
    public static final String TEXT_MUTED  = "#757575";
    public static final String BORDER      = "#E0E0E0";
    public static final String SUCCESS     = "#388E3C";
    public static final String DANGER      = "#C62828";

    // Root container
    public static final String ROOT =
            "-fx-background-color: " + BG + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;";

    // Header bar
    public static final String HEADER =
            "-fx-background-color: " + ACCENT + "; -fx-padding: 16 24;";

    public static final String HEADER_TITLE =
            "-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;";

    public static final String HEADER_SUBTITLE =
            "-fx-text-fill: rgba(255,255,255,0.75); -fx-font-size: 12px;";

    // Card / panel
    public static final String CARD =
            "-fx-background-color: " + BG_LIGHT + "; -fx-border-color: " + BORDER
            + "; -fx-border-radius: 4; -fx-background-radius: 4; -fx-padding: 20;";

    // Inputs
    public static final String INPUT =
            "-fx-background-color: white; -fx-border-color: " + BORDER
            + "; -fx-border-radius: 3; -fx-background-radius: 3;"
            + " -fx-padding: 8 10; -fx-font-size: 13px;";

    // Buttons
    public static final String BTN_PRIMARY =
            "-fx-background-color: " + ACCENT + "; -fx-text-fill: white;"
            + " -fx-font-size: 13px; -fx-padding: 9 20; -fx-cursor: hand;"
            + " -fx-border-radius: 3; -fx-background-radius: 3; -fx-font-weight: bold;";

    public static final String BTN_PRIMARY_HOVER =
            "-fx-background-color: " + ACCENT_DARK + "; -fx-text-fill: white;"
            + " -fx-font-size: 13px; -fx-padding: 9 20; -fx-cursor: hand;"
            + " -fx-border-radius: 3; -fx-background-radius: 3; -fx-font-weight: bold;";

    public static final String BTN_SECONDARY =
            "-fx-background-color: transparent; -fx-text-fill: " + ACCENT + ";"
            + " -fx-font-size: 13px; -fx-padding: 8 18; -fx-cursor: hand;"
            + " -fx-border-color: " + ACCENT + "; -fx-border-radius: 3; -fx-background-radius: 3;";

    public static final String BTN_DANGER =
            "-fx-background-color: " + DANGER + "; -fx-text-fill: white;"
            + " -fx-font-size: 12px; -fx-padding: 6 14; -fx-cursor: hand;"
            + " -fx-border-radius: 3; -fx-background-radius: 3;";

    // Labels
    public static final String LABEL =
            "-fx-font-size: 13px; -fx-text-fill: " + TEXT + ";";

    public static final String LABEL_MUTED =
            "-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";";

    public static final String SECTION_TITLE =
            "-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT + ";";

    public static final String ERROR_LABEL =
            "-fx-font-size: 12px; -fx-text-fill: " + DANGER + ";";

    public static final String SUCCESS_LABEL =
            "-fx-font-size: 12px; -fx-text-fill: " + SUCCESS + ";";

    // Table
    public static final String TABLE =
            "-fx-background-color: white; -fx-border-color: " + BORDER
            + "; -fx-border-radius: 4; -fx-background-radius: 4;";

    // Nav link
    public static final String NAV_LINK =
            "-fx-background-color: transparent; -fx-text-fill: rgba(255,255,255,0.85);"
            + " -fx-font-size: 13px; -fx-padding: 6 14; -fx-cursor: hand;"
            + " -fx-border-radius: 3; -fx-background-radius: 3;";

    public static final String NAV_LINK_ACTIVE =
            "-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white;"
            + " -fx-font-size: 13px; -fx-padding: 6 14; -fx-cursor: hand;"
            + " -fx-border-radius: 3; -fx-background-radius: 3; -fx-font-weight: bold;";

    private Styles() {}
}
