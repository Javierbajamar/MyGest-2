package com.atecresa.manualpdf;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.atecresa.application.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.util.FitPolicy;

public class ManualPDFActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_pdf);
        PDFView pdfView = findViewById(R.id.pdfView);
        getSupportActionBar().setTitle("Manual de usuario Mygest");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        try {
            //POR AHORA NO USAMOS ESTA CLASE
            pdfView.fromAsset("mygest.pdf")
                    .enableSwipe(true) // allows to block changing pages using swipe
                    .swipeHorizontal(true)
                    .enableDoubletap(true)
                    .defaultPage(0)
                    .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
                    .password(null)
                    .scrollHandle(null)
                    .enableAntialiasing(true) // improve rendering a little bit on low-res screens
                    // spacing between pages in dp. To define spacing color, set view background
                    .spacing(0)
                    .pageFitPolicy(FitPolicy.WIDTH)
                    .load();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
