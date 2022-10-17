package com.github.miagilepner.pgservice.dialogs;

import com.github.miagilepner.pgservice.data.Import;
import com.github.miagilepner.pgservice.data.PgFile;
import com.intellij.database.autoconfig.DataSourceRegistry;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.DialogWrapper;
import com.github.miagilepner.pgservice.data.PgServiceBundle;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.io.*;

public class ImportDialogWrapper extends DialogWrapper {
    private final DataSourceRegistry registry;
    TextFieldWithBrowseButton serviceButton;
    TextFieldWithBrowseButton passButton;
    private boolean pgPassEnabled;
    private final Project project;
    VirtualFile pgServiceFile;
    VirtualFile pgPassFile;

    public ImportDialogWrapper(@NotNull Project project, @NotNull DataSourceRegistry registry) {
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

    public void runImport(ImportProgressIndicator indicator) {
        doValidate();
        try {
            Import.create(registry, pgServiceFile, pgPassFile, indicator);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        String pgService = PgFile.defaultPGServiceFile();
        serviceButton = new TextFieldWithBrowseButton();
        serviceButton.setTextFieldPreferredWidth(pgService.length());
        serviceButton.setText(pgService);
        serviceButton.setEditable(true);
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleLocalFileDescriptor().withShowHiddenFiles(true);
        if (SystemInfo.isMac) {
            descriptor.setForcedToUseIdeaFileChooser(true);
        }
        serviceButton.addBrowseFolderListener(new TextBrowseFolderListener(descriptor, project));

        String pgPass = PgFile.defaultPGPassFile();
        passButton = new TextFieldWithBrowseButton();
        passButton.setText(pgPass);
        passButton.setTextFieldPreferredWidth(pgPass.length());
        passButton.setEditable(true);
        passButton.addBrowseFolderListener(new TextBrowseFolderListener(descriptor, project));
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
