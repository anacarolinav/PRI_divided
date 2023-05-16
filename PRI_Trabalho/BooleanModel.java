import java.util.*;

public class BooleanModel {
    private Map<String, List<Integer>> indiceInvertido;
    private List<Documento> documentos;

    public BooleanModel(Map<String, List<Integer>> indiceInvertido, List<Documento> documentos) {
        this.indiceInvertido = indiceInvertido;
        this.documentos = documentos;

    }

    public List<Integer> consultar(String consulta) {
        String[] termos = consulta.split("\\s+");

        List<Integer> resultados = new ArrayList<>();

        for (String termo : termos) {
            List<Integer> docIDs = indiceInvertido.get(termo);
            if (docIDs != null) {
                resultados.addAll(docIDs);
            }
        }

        return resultados;
    }

    public List<Integer> consultarComAND(String consulta) {
        String[] termos = consulta.split("\\s+");

        List<Integer> resultados = new ArrayList<>();

        if (termos.length > 0) {
            resultados.addAll(indiceInvertido.getOrDefault(termos[0], Collections.emptyList()));
        }

        for (int i = 1; i < termos.length; i++) {
            List<Integer> docIDs = indiceInvertido.getOrDefault(termos[i], Collections.emptyList());
            resultados.retainAll(docIDs);
        }

        return resultados;
    }

    public List<Integer> consultarComOR(String consulta) {
        String[] termos = consulta.split("\\s+");
    
        Set<Integer> resultados = new HashSet<>();
    
        for (String termo : termos) {
            List<Integer> docIDs = indiceInvertido.getOrDefault(termo, Collections.emptyList());
            resultados.addAll(docIDs);
        }
    
        return new ArrayList<>(resultados);
    }
    

    public List<String> consultarComNOT(String consulta) {
        String[] termos = consulta.split("\\s+");

        Set<Integer> docIDsConsulta = new HashSet<>();
        Set<Integer> resultados = new HashSet<>();

        for (String termo : termos) {
            List<Integer> docIDs = indiceInvertido.getOrDefault(termo, Collections.emptyList());
            docIDsConsulta.addAll(docIDs);
        }

        for (String termo : indiceInvertido.keySet()) {
            List<Integer> docIDs = indiceInvertido.get(termo);
            for (int docID : docIDs) {
                if (!docIDsConsulta.contains(docID)) {
                    resultados.add(docID);
                }
            }
        }

        List<String> nomesDocumentos = new ArrayList<>();
        for (int docID : resultados) {
            String nomeDocumento = obterNomeDocumento(docID);
            nomesDocumentos.add(nomeDocumento);
        }

        return nomesDocumentos;
    }

    public List<String> consultarComORNOT(String consulta) {
        String[] termos = consulta.split("\\s+");
    
        Set<Integer> docIDsConsulta = new HashSet<>();
        Set<Integer> resultados = new HashSet<>();
    
        for (String termo : termos) {
            List<Integer> docIDs = indiceInvertido.getOrDefault(termo, Collections.emptyList());
            docIDsConsulta.addAll(docIDs);
        }
    
        for (String termo : indiceInvertido.keySet()) {
            List<Integer> docIDs = indiceInvertido.get(termo);
            for (int docID : docIDs) {
                resultados.add(docID);
            }
        }
    
        //resultados.removeAll(docIDsConsulta);
    
        List<String> nomesDocumentos = new ArrayList<>();
        for (int docID : resultados) {
            String nomeDocumento = obterNomeDocumento(docID);
            nomesDocumentos.add(nomeDocumento);
        }
    
        return nomesDocumentos;
    }
    

    // Método para obter o nome do documento com base no ID do documento
    public String obterNomeDocumento(int docID) {
        for (Documento documento : documentos) {
            if (documento.getId() == docID) {
                return documento.getNome();
            }
        }
        return ""; // ou lançar uma exceção, se necessário
    }


}

