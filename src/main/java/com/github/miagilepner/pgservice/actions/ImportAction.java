package com.github.miagilepner.pgservice.actions;

import com.github.miagilepner.pgservice.data.PgServiceBundle;
import com.github.miagilepner.pgservice.dialogs.ImportProgressIndicator;
import com.github.miagilepner.pgservice.dialogs.ImportDialogWrapper;
import com.intellij.database.autoconfig.DataSourceRegistry;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;


public class ImportAction extends DumbAwareAction {
    @Override
    public ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            DataSourceRegistry registry = new DataSourceRegistry(project);
            ImportDialogWrapper dialog = new ImportDialogWrapper(project, registry);
            if (dialog.showAndGet()) {
                ProgressManager.getInstance().run(new Task.Backgroundable(project, PgServiceBundle.message("action.import.text"), true) {
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        dialog.runImport(new ImportProgressIndicator(indicator));
                    }
                    @Override public void onSuccess() {
                        registry.showDialog();
                    }
                });

            }
        }
    }

    @Override
    public void update(AnActionEvent e) {
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }
}

