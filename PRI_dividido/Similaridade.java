import java.util.*;
import java.util.Map.Entry;

public class Similaridade {
    ReadFiles readFiles = new ReadFiles();
    Map<String, Map<Integer, Set<Integer>>> indiceInvertidoCompleto = readFiles.getIndiceCompleto();
    private List<Documento> documents;

    //receber o indice invertido completo e a lista de documentos a analisar
    public Similaridade(Map<String, Map<Integer, Set<Integer>>> indiceInvertidoCompleto, List<Documento> documents) {
        this.indiceInvertidoCompleto = indiceInvertidoCompleto;
        this.documents = documents;
    }




    //este método itera através de pares de documentos, calcula a similaridade de cosseno entre eles e imprime a saída.
    public void calculateSimilarity() {
        int numDocs = documents.size();
        //constroi uma matriz: numDocs x N
        double[][] vectors = new double[numDocs][];
        //para cada doc obtem-se um vetor normalizado que é armazenado na matriz
        for (int i = 0; i < numDocs; i++) {
            vectors[i] = getNormalizedVector(documents.get(i));
        }
        //1º loop for percorre todos os docs para obter o vetor normalizado para cada doc
        for (int i = 0; i < numDocs; i++) {
            //2ºloop for para executar cada par de doc's: i diferente de j, para ser calculado para cada um apenas 1x
            for (int j = i + 1; j < numDocs; j++) {
                double similarity = cosineSimilarity(vectors[i], vectors[j]);
                System.out.printf("Similarity between %s and %s: %.4f%n", documents.get(i).getNome(),
                        documents.get(j).getNome(), similarity);
            }
        }
    }




    //este método recebe um documento e retorna o seu vetor normalizado usando a ponderação tf-idf.
    double[] getNormalizedVector(Documento document) {
        //divide o documento em termos(palavras) com o \s+, e remove a pontuação!
        String[] terms = document.getConteudo().toLowerCase().replaceAll("\\p{Punct}", "").split("\\s+");
        
        //inicializa um vetor de tamanho igual ao do indice invertido completo
        double[] vector = new double[indiceInvertidoCompleto.size()];

        //aqui um hashmap: para cada termo do doc existe a freq. da ocorrencia do mesmo
        Map<String, Integer> termFreqMap = new HashMap<>();
        for (String term : terms) {
            termFreqMap.put(term, termFreqMap.getOrDefault(term, 0) + 1);
        }

        //itera o indice invertido completo para calcular  TF-IDF(freq(termo)*idf)
        int i = 0;
        for (Entry<String, Map<Integer, Set<Integer>>> entry : indiceInvertidoCompleto.entrySet()) {
            String term = entry.getKey();
            int termFreq = termFreqMap.getOrDefault(term, 0);
            double tfidf = termFreq * Math.log((double) documents.size() / entry.getValue().size());
            vector[i++] = tfidf;
        }

        //normaliza o vetor: dividindo cada elemento pelo comprimento do vetor
        double length = Math.sqrt(Arrays.stream(vector).map(v -> v * v).sum());
        for (i = 0; i < vector.length; i++) {
            vector[i] /= length;
        }
        //vetor normalizado é retornado
        return vector;
    }



    //este método recebe dois vetores e retorna sua similaridade de cosseno.
    double cosineSimilarity(double[] v1, double[] v2) {
        double dotProduct = 0;
        //for para o produto escalar entre os 2 vetores
        for (int i = 0; i < v1.length; i++) {
            dotProduct += v1[i] * v2[i];
        }
        //resultado final=1 indica vetores identicos
        //resultado final=0 indica vetores totalmente diferentes
        return dotProduct / (norm(v1) * norm(v2));
    }


    //recebe um vetor de números reais (double) como entrada e calcula a sua norma euclidiana
    //.map() eleva cada elemento ao quadrado e depois soma-o (.sum()) e depois a raiz quadrada (sqrt)
    private double norm(double[] vector) {
        return Math.sqrt(Arrays.stream(vector).map(v -> v * v).sum());
    }
}

