class Spot implements Comparable<Spot>{
    int end;
    int cost;
    
    //no-arg »ý¼ºÀÚ
    public Spot() {
    }

    public Spot(int end, int cost) {
        this.end = end;
        this.cost = cost;
    }

    @Override
    public int compareTo(Spot o) {
        return cost - o.cost;
    }
}