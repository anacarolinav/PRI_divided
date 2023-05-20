import java.util.*;
import java.util.Map.Entry;

public class Similaridade {
    
    ReadFiles readFiles = new ReadFiles();
    Map<String, Map<Integer, Set<Integer>>> indiceInvertidoCompleto = readFiles.getIndiceCompleto();
    private List<Documento> documents;

    public Similaridade(Map<String, Map<Integer, Set<Integer>>> indiceInvertidoCompleto, List<Documento> documents) {
        this.indiceInvertidoCompleto = indiceInvertidoCompleto;
        this.documents = documents;
    }
    
    public void calculateSimilarity() {
        int numDocs = documents.size();
        double[][] vectors = new double[numDocs][];
        for (int i = 0; i < numDocs; i++) {
            vectors[i] = getNormalizedVector(documents.get(i));
        }

        for (int i = 0; i < numDocs; i++) {
            for (int j = i + 1; j < numDocs; j++) {
                double similarity = cosineSimilarity(vectors[i], vectors[j]);
                System.out.printf("Similarity between %s and %s: %.4f%n", documents.get(i).getNome(),
                        documents.get(j).getNome(), similarity);
            }
        }
    }


    double[] getNormalizedVector(Documento document) {
        String[] terms = document.getConteudo().toLowerCase().replaceAll("\\p{Punct}", "").split("\\s+");
        double[] vector = new double[indiceInvertidoCompleto.size()];
        Map<String, Integer> termFreqMap = new HashMap<>();
        for (String term : terms) {
            termFreqMap.put(term, termFreqMap.getOrDefault(term, 0) + 1);
        }
        int i = 0;
        for (Entry<String, Map<Integer, Set<Integer>>> entry : indiceInvertidoCompleto.entrySet()) {
            String term = entry.getKey();
            int termFreq = termFreqMap.getOrDefault(term, 0);
            double tfidf = termFreq * Math.log((double) documents.size() / entry.getValue().size());
            vector[i++] = tfidf;
        }
        double length = Math.sqrt(Arrays.stream(vector).map(v -> v * v).sum());
        for (i = 0; i < vector.length; i++) {
            vector[i] /= length;
        }
        return vector;
    }

    double cosineSimilarity(double[] v1, double[] v2) {
        double dotProduct = 0;
        for (int i = 0; i < v1.length; i++) {
            dotProduct += v1[i] * v2[i];
        }
        return dotProduct / (norm(v1) * norm(v2));
    }



    private double norm(double[] vector) {
        return Math.sqrt(Arrays.stream(vector).map(v -> v * v).sum());
    }
}

