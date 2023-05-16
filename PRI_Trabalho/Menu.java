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
                System.out.println("4 - Modelo Boleano");
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

                }else if (opcao == 2) {
                    System.out.println("Digite um termo:");
                    String termo = scanner.next();
                    if (indiceInvertido.containsKey(termo)) {
                        List<Integer> docIDs = indiceInvertido.get(termo);
                        System.out.println("O termo " + termo + " existe nos seguintes documentos:");
                
                        for (int docID : docIDs) {
                            String nomeDocumento = termoDocIDsMap.get(docID);
                            System.out.println("Documento: " + nomeDocumento);
                
                            // Obter as posições em que o termo aparece no documento
                            Map<Integer, Set<Integer>> docsInfo = indiceInvertidoCompleto.get(termo);
                            Set<Integer> posicoes = docsInfo.get(docID);
                            System.out.println("Posições: " + posicoes);
                        }
                    } else {
                        System.out.println("Termo não encontrado!");
                    }
                

                } else if (opcao == 3) {
                    System.out.println("Digite uma query:");
                    scanner.nextLine(); // Limpa o buffer do Scanner
                    String query = scanner.nextLine();

                    Documento queryDoc = new Documento(0, query, "Query");

                    Map<String, Double> similarityScores = new HashMap<>();

                    for (Documento doc : documents) {
                        double[] vector1 = similaridade.getNormalizedVector(queryDoc);
                        double[] vector2 = similaridade.getNormalizedVector(doc);
                        double similarity = similaridade.cosineSimilarity(vector1, vector2);
                        similarityScores.put(doc.getNome(), similarity);
                    }

                    // Ordenar os documentos por similaridade em ordem decrescente
                    List<Map.Entry<String, Double>> sortedSimilarityScores = new ArrayList<>(similarityScores.entrySet());
                    sortedSimilarityScores.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

                    // Imprimir os documentos e suas respectivas similaridades em ordem decrescente
                    System.out.println("Documentos por ordem decrescente de similaridade:");
                    for (Map.Entry<String, Double> entry : sortedSimilarityScores) {
                        String docName = entry.getKey();
                        double similarity = entry.getValue();
                        System.out.printf("Documento: %s | Similaridade: %.4f%n", docName, similarity);
                    }



                    // Imprimir os documentos e suas respectivas similaridades
                    //for (Map.Entry<String, Double> entry : sortedSimilarityScores) {
                    //    String docName = entry.getKey();
                    //   double similarity = entry.getValue();
                    //    System.out.printf("Similaridade do cosseno entre a query e %s: %.4f%n", docName, similarity);
                    //}

                }else if (opcao == 4) {
                    System.out.println("Digite o primeiro termo:");
                    String termo1 = scanner.next();
                
                    System.out.println("Digite o operador lógico (AND, OR ou NOT):");
                    String operador = scanner.next();
                
                    System.out.println("Digite o segundo termo:");
                    String termo2 = scanner.next();
                
                    BooleanModel booleanModel = new BooleanModel(indiceInvertido);
                    Set<Integer> docIDs = new HashSet<>();
                
                    if (operador.equalsIgnoreCase("AND")) {
                        docIDs.addAll(booleanModel.consultarComAND(termo1 + " " + termo2));
                    } else if (operador.equalsIgnoreCase("OR")) {
                        docIDs.addAll(booleanModel.consultarComOR(termo1 + " " + termo2));
                    } else if (operador.equalsIgnoreCase("NOT")) {
                        docIDs.addAll(booleanModel.consultarComNOT(termo1 + " " + termo2));
                    } else {
                        System.out.println("Operador lógico inválido!");
                    }
                
                    if (!docIDs.isEmpty()) {
                        System.out.println("Os termos " + termo1 + " " + operador + " " + termo2 + " existem nos seguintes documentos:");
                        System.out.println(docIDs);
                    } else {
                        System.out.println("Termos não encontrados juntos!");
                    }
                }
                

            }
        }
    }
}
