package com.github.miagilepner.pgservice.actions;

import com.github.miagilepner.pgservice.data.PgServiceBundle;
import com.github.miagilepner.pgservice.data.PgSources;
import com.intellij.database.autoconfig.DataSourceRegistry;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.io.*;


public class ImportAction extends DumbAwareAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            DataSourceRegistry registry = new DataSourceRegistry(project);
            ImportDialogWrapper dialog = new ImportDialogWrapper(project, registry);
            if (dialog.showAndGet()) {
                dialog.runImport();
            }
            registry.showDialog();
        }

    }

    @Override
    public void update(AnActionEvent e) {
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }

    public static class ImportDialogWrapper extends DialogWrapper {
        private final DataSourceRegistry registry;
        TextFieldWithBrowseButton serviceButton;
        TextFieldWithBrowseButton passButton;
        private boolean pgPassEnabled;
        private final Project project;
        VirtualFile pgServiceFile;
        VirtualFile pgPassFile;

        protected ImportDialogWrapper(@NotNull Project project, @NotNull DataSourceRegistry registry) {
            super(project);
            this.registry = registry;
            this.setTitle(PgServiceBundle.message("action.import.title"));
            this.setOKButtonText(PgServiceBundle.message("action.import.ok"));
            this.init();
            this.project = project;
        }


        @Override
        protected @Nullable ValidationInfo doValidate() {
            pgServiceFile = LocalFileSystem.getInstance().findFileByIoFile(new File(serviceButton.getText()));
            if (pgServiceFile == null) {
                return new ValidationInfo(PgServiceBundle.message("pgservice.error"), serviceButton);
            }
            if (pgPassEnabled) {
                pgPassFile = LocalFileSystem.getInstance().findFileByIoFile(new File(passButton.getText()));
                if (pgPassFile == null) {
                    return new ValidationInfo(PgServiceBundle.message("pgpass.error"), passButton).asWarning();
                }
            }
            return super.doValidate();
        }

        protected void runImport() {
            doValidate();
            try {
                PgSources.create(registry, pgServiceFile, pgPassFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected @Nullable JComponent createCenterPanel() {
            String userHome = System.getProperty("user.home");
            String pgService = PgServiceBundle.message("pgservice.default.file", userHome);
            serviceButton = new TextFieldWithBrowseButton();
            serviceButton.setTextFieldPreferredWidth(pgService.length());
            serviceButton.setText(pgService);
            serviceButton.setEditable(true);
            serviceButton.addBrowseFolderListener(new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFileDescriptor().withShowHiddenFiles(true).withShowFileSystemRoots(true), project));

            String pgPass = PgServiceBundle.message("pgpass.default.file", userHome);
            passButton = new TextFieldWithBrowseButton();
            passButton.setText(pgPass);
            passButton.setTextFieldPreferredWidth(pgPass.length());
            passButton.setEditable(true);
            passButton.addBrowseFolderListener(new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFileDescriptor().withShowHiddenFiles(true).withShowFileSystemRoots(true), project));
            passButton.setEnabled(false);

            JRadioButton usePass = new JRadioButton(PgServiceBundle.message("action.import.usepgpass"));
            JLabel passFileLabel = new JLabel(PgServiceBundle.message("action.import.pgpass"));
            passFileLabel.setEnabled(false);
            usePass.addChangeListener((ChangeEvent e) -> {
                passButton.setEnabled(usePass.isSelected());
                passFileLabel.setEnabled(usePass.isSelected());
                pgPassEnabled = usePass.isSelected();
            });
            JPanel panel = FormBuilder.createFormBuilder()
                    .addLabeledComponent(PgServiceBundle.message("action.import.pgservice"), serviceButton)
                    .addVerticalGap(5)
                    .addComponent(usePass)
                    .setFormLeftIndent(15)
                    .addLabeledComponent(passFileLabel, passButton)
                    .getPanel();
            return panel;
        }
    }
}

