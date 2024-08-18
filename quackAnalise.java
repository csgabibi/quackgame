import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class quackAnalise {

    public static void main(String[] args) {
        // definindo o caminho do arquivo
        String caminhoArquivo = "qgames.log"; // redefinir de acordo c/ patch

        try {
            // lendo o arquivo como uma única string
            String arquivo = readFile(caminhoArquivo, StandardCharsets.UTF_8);

            // analisa o arquivo e obtem a lista de partidas
            List<Partida> partidas = analisarPartidas(arquivo);

            // imprime o resultado
            System.out.println("Partidas analisadas:");
            System.out.println("__________________________");
            for (Partida partida : partidas) {
                System.out.println("Partida " + partida.getNumero());
                System.out.println("Quantidade de mortes na partida: " + partida.getQuantidadeMortes());
                for (Map.Entry<String, Integer> modEntry : partida.getMods().entrySet()) {
                    System.out.println(" " + modEntry.getKey() + ": " + modEntry.getValue());
                }
                System.out.println("__________________________");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFile(String caminhoArquivo, java.nio.charset.Charset encoding) throws IOException {
        Path path = Paths.get(caminhoArquivo);
        return Files.readString(path, encoding);
    }

    public static List<Partida> analisarPartidas(String arquivo) {
        List<Partida> partidas = new ArrayList<>();
        int numeroPartida = 1;
        int index = 0;

        while ((index = arquivo.indexOf("InitGame:", index)) != -1) {
            int fimPartida = arquivo.indexOf("ShutdownGame:", index);
            if (fimPartida == -1) {
                break;
            }

            // extrai o trecho da partida
            String trechoPartida = arquivo.substring(index, fimPartida + "ShutdownGame:".length());
            int quantidadeMortes = contarOcorrencias(trechoPartida, "Kill:");
            Map<String, Integer> mods = contarMods(trechoPartida);

            // criar o objeto Partida e adicionar a lista
            Partida partida = new Partida(numeroPartida, quantidadeMortes, mods);
            partidas.add(partida);

            // atualiza o índice e o número da partida
            index = fimPartida + "ShutdownGame:".length();
            numeroPartida++;
        }

        return partidas;
    }

    public static int contarOcorrencias(String texto, String palavra) {
        int contador = 0;
        int index = 0;

        while ((index = texto.indexOf(palavra, index)) != -1) {
            contador++;
            index += palavra.length();
        }

        return contador;
    }

    //means of death
    public static Map<String, Integer> contarMods(String texto) {
        Map<String, Integer> mods = new HashMap<>();
        String[] modTypes = {
            "MOD_UNKNOWN", "MOD_SHOTGUN", "MOD_GAUNTLET", "MOD_MACHINEGUN", 
            "MOD_GRENADE", "MOD_GRENADE_SPLASH", "MOD_ROCKET", "MOD_ROCKET_SPLASH", 
            "MOD_PLASMA", "MOD_PLASMA_SPLASH", "MOD_RAILGUN", "MOD_LIGHTNING", 
            "MOD_BFG", "MOD_BFG_SPLASH", "MOD_WATER", "MOD_SLIME", 
            "MOD_LAVA", "MOD_CRUSH", "MOD_TELEFRAG", "MOD_FALLING", 
            "MOD_SUICIDE", "MOD_TARGET_LASER", "MOD_TRIGGER_HURT", "MOD_NAIL", 
            "MOD_CHAINGUN", "MOD_PROXIMITY_MINE", "MOD_KAMIKAZE", "MOD_JUICED", 
            "MOD_GRAPPLE" 
        };

        for (String mod : modTypes) {
            int count = contarOcorrencias(texto, mod);
            if (count > 0) {
                mods.put(mod, count);
            }
        }

        return mods;
    }
}

// classe para representar uma partida
class Partida {
    private int numero;
    private int quantidadeMortes;
    private Map<String, Integer> mods;

    public Partida(int numero, int quantidadeMortes, Map<String, Integer> mods) {
        this.numero = numero;
        this.quantidadeMortes = quantidadeMortes;
        this.mods = mods;
    }

    public int getNumero() {
        return numero;
    }

    public int getQuantidadeMortes() {
        return quantidadeMortes;
    }

    public Map<String, Integer> getMods() {
        return mods;
    }
}
