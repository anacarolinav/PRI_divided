import java.util.*;

public class Menu {

    public static void main(String[] args) {

        ReadFiles readFiles = new ReadFiles();
        Map<Integer, String> termoDocIDsMap = readFiles.getTermoDocIDMap();
        Map<String, List<Integer>> indiceInvertido = readFiles.getIndiceInvertido();
        Map<String, Map<Integer, Set<Integer>>> indiceInvertidoCompleto = readFiles.getIndiceCompleto();
        List<Documento> documents = readFiles.getDocuments();

        Similaridade similaridade = new Similaridade(indiceInvertidoCompleto, documents);
        
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("MENU:");
                System.out.println("0 - Sair");
                System.out.println("1 - Obter o nome do arquivo pelo DocID");
                System.out.println("2 - Lista de identificadores de documentos dado um termo");
                System.out.println("3 - Similaridade do cosseno");
                System.out.print("Escolha uma opção: ");
                int opcao = scanner.nextInt();

                if (opcao == 0) {
                    System.out.println("Saindo...");
                    break;
                }
                else if (opcao == 1) {
                    System.out.print("Digite o ID do documento: ");
                    int docIdUser = scanner.nextInt();
                    if (termoDocIDsMap.containsKey(docIdUser)) {
                        System.out.println("Este docID corresponde a " + termoDocIDsMap.get(docIdUser));
                    } else {
                        System.out.println("ID de documento inválido!");
                    }

                } else if (opcao == 2) {
                    System.out.println("Digite um termo:");
                    String termo = scanner.next();
                    if (indiceInvertido.containsKey(termo)) {
                        System.out.println("O termo " + termo + " existe no doc "
                                + Arrays.toString(indiceInvertido.get(termo).toArray()));
                    } else {
                        System.out.println("Termo não encontrado!");
                    }

                } else if (opcao == 3) {
                    System.out.println("Digite o nome do primeiro documento:");
                    String nomeDoc1 = scanner.next();
                    System.out.println("Digite o nome do segundo documento:");
                    String nomeDoc2 = scanner.next();

                    Documento doc1 = null;
                    Documento doc2 = null;

                    for (Documento doc : documents) {
                        if (doc.getNome().equals(nomeDoc1)) {
                            doc1 = doc;
                        } else if (doc.getNome().equals(nomeDoc2)) {
                            doc2 = doc;
                        }
                    }

                    if (doc1 == null || doc2 == null) {
                        System.out.println("Um ou ambos os documentos não foram encontrados!");
                    } else {
                        double[] vector1 = similaridade.getNormalizedVector(doc1);
                        double[] vector2 = similaridade.getNormalizedVector(doc2);
                        double similarity = similaridade.cosineSimilarity(vector1, vector2);
                        System.out.printf("Similaridade do cosseno entre %s e %s: %.4f%n", doc1.getNome(), doc2.getNome(), similarity);
                    }
                }
            }
        }
        
    }
    
}
