package com.g04.cityfix.domain;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.g04.cityfix.CityFixApplication;
import com.g04.cityfix.common.utils.searchparse.IAstNode;
import com.g04.cityfix.common.utils.searchparse.Parser;
import com.g04.cityfix.common.utils.searchtoken.Tokenizer;
import com.g04.cityfix.data.model.RepairReport;
import com.g04.cityfix.data.repository.ReportRepository;
import com.google.mediapipe.tasks.components.containers.Embedding;
import com.google.mediapipe.tasks.core.BaseOptions;
import com.google.mediapipe.tasks.core.Delegate;
import com.google.mediapipe.tasks.text.textembedder.TextEmbedder;
import com.google.mediapipe.tasks.text.textembedder.TextEmbedder.TextEmbedderOptions;
import com.google.mediapipe.tasks.text.textembedder.TextEmbedderResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Junhao Liu
 */
public class SearchService {
    private final ReportRepository reportRepository;
    private TextEmbedder embedder;

    public SearchService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    private TextEmbedder getEmbedder(Context context) {
        if (this.embedder == null) {
            try {
                BaseOptions baseOptions = BaseOptions.builder()
                        .setModelAssetPath("universal_sentence_encoder.tflite")
//                    .setModelAssetPath("bert_embedder.tflite")
                        .setDelegate(Delegate.GPU)
                        .build();
                TextEmbedderOptions options = TextEmbedderOptions.builder()
                        .setBaseOptions(baseOptions)
                        .build();
                this.embedder = TextEmbedder.createFromOptions(context, options);
            } catch (Exception e) {
                Log.e("CityFix", e.toString());
                throw e;
            }
        }
        return this.embedder;
    }

    class EmbeddingTask {
        Executor executor;
        TextEmbedder embedder;
        public EmbeddingTask(Executor executor, TextEmbedder embedder) {
            this.executor = executor;
            this.embedder = embedder;
        }

        public void asyncRun(List<RepairReport> reports, String str, Consumer<List<RepairReport>> callback) {
            executor.execute(() -> {
                callback.accept(task(reports, str));
            });
        }

        private List<RepairReport> task(List<RepairReport> reports, String str) {
            Embedding embSearch = embedder.embed(str)
                    .embeddingResult()
                    .embeddings().get(0);
            return reports.stream()
                    .limit(100)
                    .map(rp -> {
                        Embedding embReport = embedder.embed(
                                        "Title: " + rp.getTitle() +
                                                "\nUsername: " + rp.getCitizenUsername() +
                                                "\nDescription: " + rp.getDescription())
                                .embeddingResult().embeddings().get(0);
                        double sim = TextEmbedder.cosineSimilarity(embSearch, embReport);
                        return new Pair<>(sim, rp);
                    })
                    .sorted(Comparator.comparing(pr -> pr.first))
                    .limit(10)
                    .map(pr -> pr.second)
                    .collect(Collectors.toList());
        }
    }

    public void search(Context context, String searchString, Consumer<List<RepairReport>> callback) {
        IAstNode astRoot = Parser.parse(Tokenizer.tokenize(searchString));
        this.reportRepository.getAllReports(repairReports ->  {
            List<RepairReport> result = repairReports.stream()
                    .filter(astRoot::check)
                    .collect(Collectors.toList());
            if (result.isEmpty()) {
                TextEmbedder embedder = this.getEmbedder(context);
                new EmbeddingTask(CityFixApplication.getExecutorService(), embedder)
                        .asyncRun(repairReports, searchString, callback);
            } else {
                callback.accept(result);
            }
        });
    }
}
