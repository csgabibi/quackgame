import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Enum para representar os tipos de MODs
enum MOD {
    MOD_UNKNOWN,
    MOD_SHOTGUN,
    MOD_GAUNTLET,
    MOD_MACHINEGUN,
    MOD_GRENADE,
    MOD_GRENADE_SPLASH,
    MOD_ROCKET,
    MOD_ROCKET_SPLASH,
    MOD_PLASMA,
    MOD_PLASMA_SPLASH,
    MOD_RAILGUN,
    MOD_LIGHTNING,
    MOD_BFG,
    MOD_BFG_SPLASH,
    MOD_WATER,
    MOD_SLIME,
    MOD_LAVA,
    MOD_CRUSH,
    MOD_TELEFRAG,
    MOD_FALLING,
    MOD_SUICIDE,
    MOD_TARGET_LASER,
    MOD_TRIGGER_HURT,
    MOD_NAIL,
    MOD_CHAINGUN,
    MOD_PROXIMITY_MINE,
    MOD_KAMIKAZE,
    MOD_JUICED,
    MOD_GRAPPLE
}

public class quackAnalise {

    public static void main(String[] args) {
        // definindo o caminho do arquivo
        String caminhoArquivo = "qgames.log"; // define o diretorio do arquivo de log

        try {
            // lendo o conteúdo do arquivo 
            String arquivo = readFile(caminhoArquivo, StandardCharsets.UTF_8);

            // Analisando o arquivo e obtendo a lista de partidas
            List<Partida> partidas = analisarPartidas(arquivo);

            // total de partidas analisadas
            int totalPartidas = partidas.size();

            // Imprimindo o total de partidas analisadas
            System.out.println("\nBem vindo à Análise do Jogo Quack!\n");
            System.out.println("Total de partidas analisadas: " + totalPartidas);
            System.out.println("__________________________");

            // detalhes de cada partida
            for (Partida partida : partidas) {
                System.out.println("\nPartida " + partida.getNumero());
                System.out.println("Quantidade de mortes na partida: " + partida.getQuantidadeMortes());
                for (Map.Entry<MOD, Integer> modEntry : partida.getMods().entrySet()) {
                    if (modEntry.getValue() > 0) { // exibe mortes > 0
                        System.out.println(" " + modEntry.getKey() + ": " + modEntry.getValue());
                    }
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
            int fimPartida = arquivo.indexOf("InitGame:", index + 1);
            if (fimPartida == -1) {
                fimPartida = arquivo.length();
            }

            // extrai o trecho da partida
            String trechoPartida = arquivo.substring(index, fimPartida);
            int quantidadeMortes = contarOcorrencias(trechoPartida, "Kill:");
            Map<MOD, Integer> mods = contarMods(trechoPartida);

            // cria o objeto Partida e adiciona à lista
            Partida partida = new Partida(numeroPartida, quantidadeMortes, mods);
            partidas.add(partida);

            // count
            index = fimPartida;
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

    public static Map<MOD, Integer> contarMods(String texto) {
        Map<MOD, Integer> mods = new EnumMap<>(MOD.class);

        // inicializa todos os MODs com 0
        for (MOD mod : MOD.values()) {
            mods.put(mod, 0);
        }

        // padrão para encontrar MODs com expressões regulares
        Pattern pattern = Pattern.compile("MOD_(\\w+)");
        Matcher matcher = pattern.matcher(texto);

        // conta cada MOD
        while (matcher.find()) {
            String modName = matcher.group(0);
            MOD mod;
            try {
                mod = MOD.valueOf(modName);
            } catch (IllegalArgumentException e) {
                continue;
            }

            mods.put(mod, mods.get(mod) + 1);
        }

        return mods;
    }
}

// classe para representar uma partida
class Partida {
    private int numero;
    private int quantidadeMortes;
    private Map<MOD, Integer> mods;

    public Partida(int numero, int quantidadeMortes, Map<MOD, Integer> mods) {
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

    public Map<MOD, Integer> getMods() {
        return mods;
    }
}
