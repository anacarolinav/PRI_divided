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
                System.out.println("5 - NOT");
                System.out.println("6 - AND NOT");
                System.out.println("7 - OR NOT");
                System.out.print("Escolha uma opção: ");
                int opcao = scanner.nextInt();

                if (opcao == 0) {
                    System.out.println("Saindo...");
                    break;
                } else if (opcao == 1) {
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
                    List<Map.Entry<String, Double>> sortedSimilarityScores = new ArrayList<>(
                            similarityScores.entrySet());
                    sortedSimilarityScores.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

                    // Imprimir os documentos e suas respectivas similaridades em ordem decrescente
                    System.out.println("Documentos por ordem decrescente de similaridade:");
                    for (Map.Entry<String, Double> entry : sortedSimilarityScores) {
                        String docName = entry.getKey();
                        double similarity = entry.getValue();
                        System.out.printf("Documento: %s | Similaridade: %.4f%n", docName, similarity);
                    }

                    // Imprimir os documentos e suas respectivas similaridades
                    // for (Map.Entry<String, Double> entry : sortedSimilarityScores) {
                    // String docName = entry.getKey();
                    // double similarity = entry.getValue();
                    // System.out.printf("Similaridade do cosseno entre a query e %s: %.4f%n",
                    // docName, similarity);
                    // }

                } else if (opcao == 4) {
                    System.out.println("Digite a consulta no formato 'termo1 OPERADOR termo2':");
                    scanner.nextLine();
                    String consulta = scanner.nextLine();
                    String[] consultaArray = consulta.split(" ");

                    if (consultaArray.length != 3) {
                        System.out.println("Consulta inválida!");
                    } else {
                        String termo1 = consultaArray[0];
                        String operador = consultaArray[1];
                        String termo2 = consultaArray[2];

                        BooleanModel booleanModel = new BooleanModel(indiceInvertido, documents);
                        List<Integer> docIDs = new ArrayList<>();

                        if (operador.equalsIgnoreCase("AND")) {
                            docIDs = booleanModel.consultarComAND(termo1 + " " + termo2);
                        } else if (operador.equalsIgnoreCase("OR")) {
                            docIDs = booleanModel.consultarComOR(termo1 + " " + termo2);

                        } else {
                            System.out.println("Operador lógico inválido!");
                        }

                        if (!docIDs.isEmpty()) {
                            System.out.println("Os termos " + termo1 + " " + operador + " " + termo2
                                    + " existem nos seguintes documentos:");

                            List<String> nomesDocumentos = new ArrayList<>();
                            for (int docID : docIDs) {
                                String nomeDocumento = booleanModel.obterNomeDocumento(docID);
                                nomesDocumentos.add(nomeDocumento);
                            }

                            System.out.println(nomesDocumentos);
                        } else {
                            System.out.println("Termos não encontrados juntos!");
                        }
                    }
                } else if (opcao == 5) {
                    System.out.println("Digite a consulta no formato 'termo1 NOT termo2':");
                    scanner.nextLine();
                    String consulta = scanner.nextLine();

                    BooleanModel booleanModel = new BooleanModel(indiceInvertido, documents);
                    List<String> nomesDocumentos = booleanModel.consultarComNOT(consulta);

                    if (!nomesDocumentos.isEmpty()) {
                        System.out.println("Os termos " + consulta + " existem nos seguintes documentos:");
                        System.out.println(nomesDocumentos);
                    } else {
                        System.out.println("Termos não encontrados!");
                    }

                } else if (opcao == 6) {
                    System.out.println("Digite os termos da consulta no formato 'termo1 AND NOT termo2':");
                    scanner.nextLine();
                    String consulta = scanner.nextLine();
                    String[] consultaArray = consulta.split(" ");

                    if (consultaArray.length != 4) {
                        System.out.println("Consulta inválida!");
                    } else {
                        String termo1 = consultaArray[0];
                        String operador = consultaArray[1];
                        String operadorNOT = consultaArray[2];
                        String termo2 = consultaArray[3];

                        BooleanModel booleanModel = new BooleanModel(indiceInvertido, documents);
                        List<String> docIDsNaoTermo2 = booleanModel.consultarComNOT(termo2);
                        System.out.println(docIDsNaoTermo2);

                        List<String> docIDsTermo1 = new ArrayList<>();
                        for (String termo : termo1.split("\\s+")) {
                            List<Integer> docIDs = indiceInvertido.getOrDefault(termo, Collections.emptyList());
                            for (int docID : docIDs) {
                                docIDsTermo1.add(booleanModel.obterNomeDocumento(docID));
                            }
                        }
                        System.out.println(docIDsTermo1);

                        List<String> nomesDocumentos = new ArrayList<>();
                        for (String docID : docIDsTermo1) {
                            if (docIDsNaoTermo2.contains(docID)) {
                                nomesDocumentos.add(docID);
                            }
                        }

                        if (!nomesDocumentos.isEmpty()) {
                            System.out.println("Os termos " + termo1 + " " + operador + " " + operadorNOT + termo2
                                    + " existem nos seguintes documentos:");

                            System.out.println(nomesDocumentos);
                        } else {
                            System.out.println("Termos não encontrados juntos!");
                        }
                    }
                } else if (opcao == 7) {
                    System.out.println("Digite os termos da consulta no formato 'termo1 OR NOT termo2':");
                    scanner.nextLine();
                    String consulta = scanner.nextLine();
                    String[] consultaArray = consulta.split(" ");

                    if (consultaArray.length != 4) {
                        System.out.println("Consulta inválida!");
                    } else {
                        String termo1 = consultaArray[0];
                        String operador = consultaArray[1];
                        String operadorNOT = consultaArray[2];
                        String termo2 = consultaArray[3];

                        BooleanModel booleanModel = new BooleanModel(indiceInvertido, documents);
                        List<String> docIDsNaoTermo2 = booleanModel.consultarComNOT(termo2);
                        System.out.println(docIDsNaoTermo2);

                        List<String> docIDsTermo1 = new ArrayList<>();
                        for (String termo : termo1.split("\\s+")) {
                            List<Integer> docIDs = indiceInvertido.getOrDefault(termo, Collections.emptyList());
                            for (int docID : docIDs) {
                                docIDsTermo1.add(booleanModel.obterNomeDocumento(docID));
                            }
                        }
                        System.out.println(docIDsTermo1);

                        Set<String> nomesDocumentos = new HashSet<>();
                        nomesDocumentos.addAll(docIDsTermo1);
                        nomesDocumentos.addAll(docIDsNaoTermo2);

                        if (!nomesDocumentos.isEmpty()) {
                            System.out.println("Os termos " + termo1 + " " + operador + " " + operadorNOT + termo2
                                    + " existem nos seguintes documentos:");

                            System.out.println(nomesDocumentos);
                        } else {
                            System.out.println("Termos não encontrados juntos!");
                        }
                    }
                }

            }
        }
    }
}
