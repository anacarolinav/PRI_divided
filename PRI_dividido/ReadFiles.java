import java.io.*;
import java.util.*;

public class ReadFiles {
    private static final String CAMINHO_PASTA = "/Users/anacarolinaalves/Downloads/tentativa_PRI/Ficheiros";

    public List<Documento> documentos;
    private File[] arquivos;

    public ReadFiles() {
        File pasta = new File(CAMINHO_PASTA);
        arquivos = pasta.listFiles();

        Arrays.sort(arquivos, new Comparator<File>() {
            public int compare(File f1, File f2) {
                return Long.compare(f1.length(), f2.length());
            }
        });

        documentos = new ArrayList<>();
        int id = 1;
        for (File arquivo : arquivos) {
            if (arquivo.isFile()) {
                String nomeArquivo = arquivo.getName();
                try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
                    StringBuilder conteudo = new StringBuilder();
                    String linha;
                    while ((linha = br.readLine()) != null) {
                        conteudo.append(linha);
                        conteudo.append(System.lineSeparator());
                    }
                    Documento documento = new Documento(id, conteudo.toString(), nomeArquivo);
                    documentos.add(documento);
                    id++;
                } catch (IOException e) {
                    System.out.println("Erro ao ler o arquivo " + arquivo.getName() + ": " + e.getMessage());
                }
            }
        }
        
    }





    public List<Documento> getDocuments() {
        return documentos;
    }





    //este hashMap dá  {1:nome_doc1,2:nome_doc2,...}
    public Map<Integer, String> getTermoDocIDMap() {
        Map<Integer, String> termoDocIDsMap = new HashMap<>();
        for (Documento documento : documentos) {
            termoDocIDsMap.put(documento.getId(), documento.getNome());
        }
        return termoDocIDsMap;
    }


    //vai retonar um indice na forma:{key:value, termo1:doc1, termo rato: aparece no doc1, e no doc3}
    public Map<String, List<Integer>> getIndiceInvertido() {
        Map<String, List<Integer>> indiceInvertido = new HashMap<>();

        for (Documento documento : documentos) {
            String[] termos = documento.getConteudo().toLowerCase().replaceAll("[^a-z\\s]", "").split("\\s+");

            for (String termo : termos) {
                List<Integer> ids = indiceInvertido.getOrDefault(termo, new ArrayList<>());
                if (!ids.contains(documento.getId())) {
                    ids.add(documento.getId());
                    indiceInvertido.put(termo, ids);
                }
            }
        }

        // Imprime o índice invertido
        for (Map.Entry<String, List<Integer>> entrada : indiceInvertido.entrySet()) {
            String termo = entrada.getKey();
            List<Integer> ids = entrada.getValue();
            System.out.print(termo + ": ");
            for (int id : ids) {
                System.out.print(id + " ");
            }
            System.out.println();
        }

        return indiceInvertido;

    }

    //retorna o indice invertido: {termo1: {docID : posição}, "girafa": {aparece no doc34: aparece na posição 2,33,104}}
    public Map<String, Map<Integer, Set<Integer>>> getIndiceCompleto() {
        Map<String, Map<Integer, Set<Integer>>> indiceInvertidoCompleto = new HashMap<>();

        // Cria o índice invertido complexo
        for (Documento documento : documentos) {
            String[] termos = documento.getConteudo().toLowerCase().replaceAll("\\p{Punct}", "").split("\\s+");
            int docId = documento.getId();

            for (int i = 0; i < termos.length; i++) {
                String termo = termos[i];
                Map<Integer, Set<Integer>> docsInfo = indiceInvertidoCompleto.getOrDefault(termo, new HashMap<>());
                Set<Integer> posicoesTermo = docsInfo.getOrDefault(docId, new HashSet<>());
                posicoesTermo.add(i);
                docsInfo.put(docId, posicoesTermo);
                indiceInvertidoCompleto.put(termo, docsInfo);
            }
        }

        // Cria o mapa de frequência de documentos para cada termo
        Map<String, Integer> freqDocMap = new HashMap<>();
        for (String termo : indiceInvertidoCompleto.keySet()) {
            Map<Integer, Set<Integer>> docsInfo = indiceInvertidoCompleto.get(termo);
            int freqDoc = docsInfo.size();
            freqDocMap.put(termo, freqDoc);
        }

        // Imprime o índice invertido completo
        for (Map.Entry<String, Map<Integer, Set<Integer>>> entrada : indiceInvertidoCompleto.entrySet()) {
            String termo = entrada.getKey();
            Map<Integer, Set<Integer>> docsInfo = entrada.getValue();
            int freqDoc = freqDocMap.get(termo);
            System.out.print(termo + ": {" + freqDoc + "} ");
            for (Map.Entry<Integer, Set<Integer>> docInfo : docsInfo.entrySet()) {
                System.out.print(docInfo.getKey() + ": " + docInfo.getValue() + " ");
            }
            System.out.println();
        }

        // Escreve o índice invertido completo em um arquivo
        try {
            PrintWriter writer = new PrintWriter(new FileWriter("output.txt"));
            for (Map.Entry<String, Map<Integer, Set<Integer>>> entrada : indiceInvertidoCompleto.entrySet()) {
                String termo = entrada.getKey();
                Map<Integer, Set<Integer>> docsInfo = entrada.getValue();
                int freqDoc = freqDocMap.get(termo);
                writer.print(termo + ": {" + freqDoc + "} ");
                for (Map.Entry<Integer, Set<Integer>> docInfo : docsInfo.entrySet()) {
                    writer.print(docInfo.getKey() + ": " + docInfo.getValue() + " ");
                }
                writer.println();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return indiceInvertidoCompleto;
    }


    /* 
    public static void main(String[] args) {
        ReadFiles leitor = new ReadFiles();
        leitor.getIndiceCompleto();
    }
    */

}
