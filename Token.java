public class Token implements Comparable<Token> {
  String name;
  private int count;

  public Token(String name) {
    this.name = name;
    this.count = 1;
  }

  public int getCount() {
    return this.count;
  }

  public void inc(int i) {
    this.count += i;
  }

  @Override
  public int compareTo(Token t2) {
    return this.count - t2.count;
  }

  public double distance(Token t2) {
    return Helper.strSimilarity(this.name, t2.name);
  }

  @Override
  public String toString() {
    return name + ":" + count;
  }
}
