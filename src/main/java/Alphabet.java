import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.joining;

class Graph {
    private final List<LinkedList<Integer>> adj;
    private final int n;

    Graph(int nOfV) {
        n = nOfV;
        adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new LinkedList<>());
        }
    }

    public void addEdge(int f, int t) {
        adj.get(f).add(t);
    }

    private void topoInner(int curr, boolean[] vis, Stack<Integer> st) {
        vis[curr] = true;
        for (int adjToCurr: adj.get(curr)) {
            if (!vis[adjToCurr]) {
                topoInner(adjToCurr, vis, st);
            }
        }
        st.push(curr);
    }

    public Integer[] topo() {
        Stack<Integer> st = new Stack<>();
        boolean[] vis = new boolean[n];
        Arrays.fill(vis, false);
        for (int i = 0; i < n; i++) {
            if (!vis[i]) {
                topoInner(i, vis, st);
            }
        }
        var ret = new Integer[n];
        for (int i = 0; i < n; i++) {
            ret[i] = st.pop();
        }
        return ret;
    }

    private boolean hasCycle(int curr, boolean[] tempVis, boolean[] vis) {
        tempVis[curr] = true;
        for (int adjToCurr: adj.get(curr)) {
            if (tempVis[adjToCurr]) {
                return true;
            }
            if (!vis[adjToCurr] && hasCycle(adjToCurr, tempVis, vis)) {
                return true;
            }
        }
        tempVis[curr] = false;
        vis[curr] = true;
        return false;
    }

    public boolean hasCycles() {
        boolean[] vis = new boolean[n];
        Arrays.fill(vis, false);
        for (int i = 0; i < n; i++) {
            boolean[] tempVis = new boolean[n];
            Arrays.fill(tempVis, false);
            if (!vis[i] && hasCycle(i, tempVis, vis)) {
                return true;
            }
        }
        return false;
    }
}

public class Alphabet implements Runnable {
    public static void main(String[] args) {
        new Thread(new Alphabet()).start();
    }

    public static String alphabet(List<String> nameList) {
        Graph graph = new Graph(26);
        for (int i = 0; i < nameList.size() - 1; i++) {
            String firstWord = nameList.get(i);
            String secWord = nameList.get(i + 1);
            for (int j = 0; j < Math.min(firstWord.length(), secWord.length()); j++) {
                if (firstWord.charAt(j) != secWord.charAt(j)) {
                    graph.addEdge(firstWord.charAt(j) - 'a', secWord.charAt(j)- 'a');
                    break;
                }
            }
        }
        if (graph.hasCycles()) {
            return "Impossible";
        }
        return Arrays.stream(graph.topo())
                .map(i -> (char)('a' + i))
                .collect(toList())
                .stream()
                .map(String::valueOf)
                .collect(joining());
    }

    BufferedReader br;
    StringTokenizer st;
    PrintWriter out;
    boolean eof = false;

    String nextToken() {
        while (st == null || !st.hasMoreTokens()) {
            try {
                st = new StringTokenizer(br.readLine());
            } catch (Exception e) {
                eof = true;
                return "0";
            }
        }
        return st.nextToken();
    }

    public void run() {
        Locale.setDefault(Locale.US);
        try {
            br = new BufferedReader(new InputStreamReader(System.in));
            out = new PrintWriter(System.out);
            solve();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(566);
        }
    }

    private void solve() {
        int n = Integer.parseInt(nextToken());
        List<String> nameList = new ArrayList<>();
        for(int i = 0; i < n; i++) {
            nameList.add(nextToken());
        }
        out.println(alphabet(nameList));
    }
}
