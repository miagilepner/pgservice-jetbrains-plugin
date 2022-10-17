package com.github.miagilepner.pgservice.dialogs;

import com.github.miagilepner.pgservice.data.PgServiceBundle;
import com.intellij.openapi.progress.ProgressIndicator;
import lombok.Setter;

public class ImportProgressIndicator {
    private final ProgressIndicator indicator;
    @Setter private int numSections;
    private int readSections;

    public ImportProgressIndicator(ProgressIndicator indicator) {
        indicator.setText(PgServiceBundle.message("progress.text"));
        this.indicator = indicator;
    }
    public void addSection() {
        readSections++;
        indicator.setFraction(readSections/numSections);
    }

    public void done() {
        indicator.setFraction(1);
    }
}
