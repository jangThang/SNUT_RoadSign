import java.io.*;
import java.util.*;

public class Path {
    private static int INFINITE = 1000000; // ���ͽ�Ʈ�� �˰����� ���� �ʱⰪ
    private static int dist[]; // ����ġ(�Ÿ�)
    private static final int NODE = 17; // ���(�б� ���) ����
    private static final int EDGE = 26; // ����(�뼱) ����
    private static int parent[];
    private static int spotCnt; // ��θ� ���� ��� ��
    
    private int startNode; // �����
    private int endNode; // ������
    private String pathNode; // ���
    private int meter; // �� �Ÿ�
    private int trableTime; // ���� ������ ���� �ʿ�ð�
    
    // no-arg ������
    public Path() {
    }
    
    public Path(int en) {
    	endNode = en;
    }
    
    public Path(int sn, int en) {
    	startNode = sn;
    	endNode = en;
    }

    public void calPath() throws IOException {
        
    	File sourceFile = new File("path.txt");
        if (!sourceFile.exists()) {
          System.out.println("path ������ �������� �ʽ��ϴ�.");
          System.exit(2);
        }
        ArrayList<Spot> SpotList[];
        SpotList = new ArrayList[NODE + 1];

        // ��������Ʈ �ʱ�ȭ
        for(int i = 1 ; i <= NODE; i++){
            SpotList[i] = new ArrayList<>();
        }
        
        Scanner input = new Scanner(sourceFile);

        // path������ �������� �Ÿ����� SpotList�� �����մϴ�.
        while(input.hasNext()){
            StringTokenizer st = new StringTokenizer(input.nextLine());

            int start = Integer.parseInt(st.nextToken());
            int end = Integer.parseInt(st.nextToken());
            int cost = Integer.parseInt(st.nextToken());

            // �� spot���� �Ÿ��� �߰��մϴ�.
            SpotList[start].add(new Spot(end, cost));
            SpotList[end].add(new Spot(start, cost));
        }
        input.close();

        // �ִܰ�θ� �����Ѵ�.
        dist = new int[NODE + 1];
        // ��� ������ ���� �θ��� ������ �����Ѵ�.
        parent = new int[NODE + 1];
        //�Ÿ��� ���Ѵ�� �ʱ�ȭ
        Arrays.fill(dist, INFINITE);
        spotCnt = 0;

        // ���ͽ�Ʈ�� �̿��Ͽ� �ִܰŸ��� ���մϴ�.
        PriorityQueue<Spot> pq = new PriorityQueue<>();
        boolean check[] = new boolean[NODE + 1];

        pq.add(new Spot(startNode, 0));
        dist[startNode] = 0;

        while(!pq.isEmpty()){
            Spot curSpot = pq.poll();
            int cur = curSpot.end;

            if(check[cur] == true) continue;
            check[cur] = true;

            for(Spot Spot : SpotList[cur]){
                if(dist[Spot.end] > dist[cur] + Spot.cost){
                    dist[Spot.end] = dist[cur] + Spot.cost;
                    pq.add(new Spot(Spot.end, dist[Spot.end]));
                    parent[Spot.end] = cur;
                }
            }
        }
        
        Stack<Integer> stack = searchPath(startNode, endNode);

        StringBuilder sb = new StringBuilder();
        while(!stack.isEmpty()){
            int spot = stack.pop();
            sb.append(spot + " ");
        }
        
        meter = dist[endNode];
        pathNode = sb.toString();
        trableTime = (int)(meter/1.1); // �ʼ� 1.1m/s�� ������� ��
    }
    
    public static Stack<Integer> searchPath(int startNode, int endNode){
        Stack<Integer> stack = new Stack<>();
        int cur = endNode;

        while(cur != startNode) {
            stack.push(cur);
            spotCnt++;

            cur = parent[cur];
        }
        stack.push(cur);
        spotCnt++;

        return stack;
    }
    
    public int getMeter() {
    	return meter;
    }
    public String getPathNode() {
    	return pathNode;
    }
    public int getNodeCount() {
    	return spotCnt;
    }
    public int getTime() {
    	return trableTime;
    }
}