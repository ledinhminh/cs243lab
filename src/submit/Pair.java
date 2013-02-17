package submit;


public class Pair implements Comparable<Pair> {

        private final String left;
        private final String right;

        public Pair(String left, String right) {
            this.left = left;
            this.right = right;
        }

        public String getLeft() { return left; }
        public String getRight() { return right; }

        @Override
        public int hashCode() { 
            return left.hashCode() ^ right.hashCode(); 
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) return false;
            if (!(o instanceof Pair)) return false;
            Pair p = (Pair) o;
            return this.left.equals(p.getLeft()) &&
                this.right.equals(p.getRight());
        }


        public int compareTo(Pair p){
            int lc=this.left.compareTo(p.getLeft());
            if(lc==0){
                return this.right.compareTo(p.getRight());
            }else{
                return lc;
            }
        }
        
        @Override
        public String toString(){
            return "["+left+","+right+"]";
        }
}
