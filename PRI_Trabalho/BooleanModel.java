import java.util.*;

public class BooleanModel {
    private Map<String, List<Integer>> indiceInvertido;

    public BooleanModel(Map<String, List<Integer>> indiceInvertido) {
        this.indiceInvertido = indiceInvertido;
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

        List<Integer> resultados = new ArrayList<>();

        for (String termo : termos) {
            List<Integer> docIDs = indiceInvertido.getOrDefault(termo, Collections.emptyList());
            resultados.addAll(docIDs);
        }

        return resultados;
    }

    public List<Integer> consultarComNOT(String consulta) {
        String[] termos = consulta.split("\\s+");

        List<Integer> resultados = new ArrayList<>();

        Set<Integer> docIDsConsulta = new HashSet<>();

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

        return resultados;
    }
}

