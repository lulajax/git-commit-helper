package com.junjie.githelper.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.UUID;

import com.junjie.githelper.model.AppConfig;
import com.junjie.githelper.model.LLMSettings;
import com.junjie.githelper.model.Project;
import com.junjie.githelper.service.ConfigService;
import com.junjie.githelper.service.GitService;
import com.junjie.githelper.service.LLMService;

public class MainViewController {

    // Left Pane
    @FXML private ListView<Project> projectListView;
    @FXML private Button addProjectButton;
    @FXML private Button removeProjectButton;

    // Center Pane
    @FXML private TextArea stagedChangesTextArea;
    @FXML private Button refreshButton;
    @FXML private TextArea commitMessageTextArea;
    @FXML private Button generateButton;
    @FXML private Button copyButton;
    @FXML private Button commitButton;

    // Right Pane
    @FXML private TextField providerTextField;
    @FXML private PasswordField apiKeyField;
    @FXML private TextField modelTextField;
    @FXML private TextField baseUrlTextField;
    @FXML private CheckBox useProxyCheckBox;
    @FXML private TextField proxyHostTextField;
    @FXML private TextField proxyPortTextField;
    @FXML private Button saveSettingsButton;
    @FXML private TextArea customPromptTextArea;
    @FXML private Button savePromptButton;

    private ConfigService configService;
    private GitService gitService;
    private LLMService llmService;
    private AppConfig appConfig;

    @FXML
    public void initialize() {
        System.out.println("MainViewController initializing...");
        configService = new ConfigService();
        gitService = new GitService();
        llmService = new LLMService();
        try {
            appConfig = configService.loadConfig();
            populateUIFromConfig();
        } catch (IOException e) {
            // Handle error (e.g., show an alert)
            e.printStackTrace();
        }
        // Listen for selection changes in the project list
        projectListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> onProjectSelected(newValue)
        );

        // Add button actions
        addProjectButton.setOnAction(event -> onAddProject());
        removeProjectButton.setOnAction(event -> onRemoveProject());
        refreshButton.setOnAction(event -> refreshStagedChanges());
        generateButton.setOnAction(event -> onGenerateCommitMessage());
        saveSettingsButton.setOnAction(event -> onSaveSettings());
        savePromptButton.setOnAction(event -> onSavePrompt());
        copyButton.setOnAction(event -> onCopy());
        commitButton.setOnAction(event -> onCommit());
        
        // Bind proxy input fields to checkbox state
        proxyHostTextField.disableProperty().bind(useProxyCheckBox.selectedProperty().not());
        proxyPortTextField.disableProperty().bind(useProxyCheckBox.selectedProperty().not());
    }

    private void onCopy() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(commitMessageTextArea.getText());
        clipboard.setContent(content);
    }

    private void onCommit() {
        Project selectedProject = projectListView.getSelectionModel().getSelectedItem();
        if (selectedProject == null) {
            // Show warning
            return;
        }
        String commitMessage = commitMessageTextArea.getText();
        if (commitMessage.isEmpty()) {
            // Show warning
            return;
        }

        try {
            gitService.commit(selectedProject, commitMessage);
            // Show success alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Commit successful!");
            alert.showAndWait();
            
            // Refresh staged changes
            refreshStagedChanges();
            commitMessageTextArea.clear();
        } catch (Exception e) {
            // Show error alert
            e.printStackTrace();
        }
    }

    private void onSaveSettings() {
        // 解析代理端口
        Integer proxyPort = null;
        if (!proxyPortTextField.getText().trim().isEmpty()) {
            try {
                proxyPort = Integer.parseInt(proxyPortTextField.getText().trim());
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Port");
                alert.setHeaderText(null);
                alert.setContentText("Proxy port must be a valid number.");
                alert.showAndWait();
                return;
            }
        }
        
        LLMSettings newSettings = new LLMSettings(
                providerTextField.getText(),
                apiKeyField.getText(),
                modelTextField.getText(),
                baseUrlTextField.getText(),
                proxyHostTextField.getText(),
                proxyPort,
                useProxyCheckBox.isSelected()
        );
        appConfig = new AppConfig(appConfig.version(), newSettings, appConfig.projects(), appConfig.selected_project_id());
        try {
            configService.saveConfig(appConfig);
            // Show confirmation alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Settings Saved");
            alert.setHeaderText(null);
            alert.setContentText("LLM and proxy settings have been saved successfully.");
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to save settings");
            alert.setContentText("An error occurred while saving the settings: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void onSavePrompt() {
        Project selectedProject = projectListView.getSelectionModel().getSelectedItem();
        if (selectedProject == null) {
            // Show warning: no project selected
            return;
        }

        String newPrompt = customPromptTextArea.getText();
        Project updatedProject = new Project(
                selectedProject.id(),
                selectedProject.name(),
                selectedProject.path(),
                newPrompt
        );

        var updatedProjects = new ArrayList<>(appConfig.projects());
        int projectIndex = updatedProjects.indexOf(selectedProject);
        if (projectIndex != -1) {
            updatedProjects.set(projectIndex, updatedProject);
            appConfig = new AppConfig(appConfig.version(), appConfig.llm_settings(), updatedProjects, appConfig.selected_project_id());
            try {
                configService.saveConfig(appConfig);
                // Refresh the list view to reflect the change
                projectListView.getItems().set(projectIndex, updatedProject);
            } catch (IOException e) {
                e.printStackTrace(); // Show error alert
            }
        }
    }

    private void onGenerateCommitMessage() {
        String diffContent = stagedChangesTextArea.getText();
        if (diffContent.isEmpty() || diffContent.startsWith("No staged changes")) {
            commitMessageTextArea.setText("There are no staged changes to generate a commit message from.");
            return;
        }

        String customPrompt = customPromptTextArea.getText();
        
        // 解析代理端口
        Integer proxyPort = null;
        if (!proxyPortTextField.getText().trim().isEmpty()) {
            try {
                proxyPort = Integer.parseInt(proxyPortTextField.getText().trim());
            } catch (NumberFormatException e) {
                commitMessageTextArea.setText("Error: Invalid proxy port number.");
                return;
            }
        }
        
        LLMSettings settings = new LLMSettings(
                providerTextField.getText(),
                apiKeyField.getText(),
                modelTextField.getText(),
                baseUrlTextField.getText(),
                proxyHostTextField.getText(),
                proxyPort,
                useProxyCheckBox.isSelected()
        );

        commitMessageTextArea.setText("Generating commit message...");
        new Thread(() -> {
            try {
                String commitMessage = llmService.generateCommitMessage(settings, customPrompt, diffContent);
                Platform.runLater(() -> commitMessageTextArea.setText(commitMessage));
            } catch (Exception e) {
                Platform.runLater(() -> commitMessageTextArea.setText("Error: " + e.getMessage()));
                e.printStackTrace();
            }
        }).start();
    }

    private void onProjectSelected(Project selectedProject) {
        if (selectedProject != null) {
            // Update the custom prompt text area
            customPromptTextArea.setText(selectedProject.custom_prompt());
            refreshStagedChanges();
            // TODO: Update and save the selected_project_id in the config
        } else {
            stagedChangesTextArea.clear();
        }
    }

    private void refreshStagedChanges() {
        Project selectedProject = projectListView.getSelectionModel().getSelectedItem();
        if (selectedProject == null) {
            stagedChangesTextArea.setText("Please select a project.");
            return;
        }

        stagedChangesTextArea.setText("Loading...");
        // Run git operation in a background thread
        new Thread(() -> {
            try {
                String changes = gitService.getStagedChanges(selectedProject);
                Platform.runLater(() -> {
                    stagedChangesTextArea.setText(changes.isEmpty() ? "No staged changes found." : changes);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    stagedChangesTextArea.setText("Error loading changes: " + e.getMessage());
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void onRemoveProject() {
        Project selectedProject = projectListView.getSelectionModel().getSelectedItem();
        if (selectedProject == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Project Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a project to remove.");
            alert.showAndWait();
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Removal");
        confirmAlert.setHeaderText("Remove '" + selectedProject.name() + "'");
        confirmAlert.setContentText("Are you sure you want to remove this project from the list? This action cannot be undone.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                var updatedProjects = new ArrayList<>(appConfig.projects());
                updatedProjects.remove(selectedProject);

                appConfig = new AppConfig(
                        appConfig.version(),
                        appConfig.llm_settings(),
                        updatedProjects,
                        appConfig.selected_project_id()
                );

                try {
                    configService.saveConfig(appConfig);
                    projectListView.getItems().setAll(appConfig.projects());
                } catch (IOException e) {
                    e.printStackTrace(); // Show error alert
                }
            }
        });
    }

    private void onAddProject() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Git Repository");
        Stage stage = (Stage) addProjectButton.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            if (isValidGitRepository(selectedDirectory)) {
                Project newProject = new Project(
                        UUID.randomUUID().toString(),
                        selectedDirectory.getName(),
                        selectedDirectory.getAbsolutePath(),
                        "Generate a conventional commit message for the following changes:"
                );

                var updatedProjects = new ArrayList<>(appConfig.projects());
                updatedProjects.add(newProject);

                appConfig = new AppConfig(
                        appConfig.version(),
                        appConfig.llm_settings(),
                        updatedProjects,
                        appConfig.selected_project_id()
                );

                try {
                    configService.saveConfig(appConfig);
                    projectListView.getItems().setAll(appConfig.projects());
                } catch (IOException e) {
                    e.printStackTrace(); // Show error alert
                }
            } else {
                // Show an alert that it's not a valid git repo
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid Directory");
                alert.setContentText("The selected directory is not a valid Git repository.");
                alert.showAndWait();
            }
        }
    }

    private boolean isValidGitRepository(File directory) {
        return Files.isDirectory(directory.toPath().resolve(".git"));
    }

    private void populateUIFromConfig() {
        // Populate Project List
        projectListView.getItems().setAll(appConfig.projects());
        projectListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Project item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.name() == null) {
                    setText(null);
                } else {
                    setText(item.name());
                }
            }
        });

        // Populate LLM Settings
        providerTextField.setText(appConfig.llm_settings().provider());
        apiKeyField.setText(appConfig.llm_settings().api_key());
        modelTextField.setText(appConfig.llm_settings().model());
        baseUrlTextField.setText(appConfig.llm_settings().base_url());
        
        // Populate Proxy Settings
        useProxyCheckBox.setSelected(Boolean.TRUE.equals(appConfig.llm_settings().use_proxy()));
        if (appConfig.llm_settings().proxy_host() != null) {
            proxyHostTextField.setText(appConfig.llm_settings().proxy_host());
        }
        if (appConfig.llm_settings().proxy_port() != null) {
            proxyPortTextField.setText(String.valueOf(appConfig.llm_settings().proxy_port()));
        }
    }
}
