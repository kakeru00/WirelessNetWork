package graph;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Vertex<T> implements Serializable{
	
	private int x;//横坐标
	private int y;//纵坐标
	private int contactRadius;//传输半径
	private T id;
	private List<Vertex<T>>  neighbors = new ArrayList<Vertex<T>>();
	private boolean black = true;
	private boolean visited = false;
	
	public Vertex() {
		
	}
	public Vertex(T vertexId) {
		this.id = vertexId;
	}
	public Vertex(boolean isBlack) {
		
		this.black = isBlack;
		
	}
	public Vertex(T vertexId, int x, int y) {
		this.id = vertexId;
		this.x = x;
		this.y = y;
	}
	public boolean isBlack() {
		return black;
	}
	public void setBlack() {
		this.black = true;
	}
	public boolean isWhite(){
		return !black;
	}
	public void setWhite() {
		this.black = false;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getRadius() {
		return contactRadius;
	}
	public void setRadius(int radius) {
		this.contactRadius = radius;
	}
	public T getId() {
		return id;
	}
	public void setId(T id) {
		this.id = id;
	}
	
	//连接顶点
	public boolean connect(Vertex<T> endVertex) {
		if(!this.equals(endVertex)){//不是同一个顶点
			for (Vertex<T> vertex : getNeighbors()) {//判断该条边是否已存在
				if(vertex.equals(endVertex)){
					return false;
				}
			}
			//双方添加邻居
			this.getNeighbors().add(endVertex);
			endVertex.getNeighbors().add(this);
			return true;
		}else return false;
		
		
	}
	
	public boolean breakOff(Vertex<T> endVertex){
		if(this.neighbors.contains(endVertex)){
			if(endVertex.getNeighbors().remove(this) && this.neighbors.remove(endVertex))
			return true;
		}
		return false;
		
	}
	
	public double distance(Vertex<T> vertex){
		int x1_x2 = this.x - vertex.getX();
		int y1_y2 = this.y - vertex.getY();
		return Math.sqrt(Math.pow(x1_x2, 2)+Math.pow(y1_y2, 2));
		
	}
	

	public boolean isVisited() {
		return visited;
	}
	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	public void visit(){
		this.visited = true;
	}
	
	public List<Vertex<T>> getNeighbors() {
		return neighbors;
	}
	public void setNeighbors(List<Vertex<T>> neighbors) {
		this.neighbors = neighbors;
	}
	
	// 点 this 是否能支配 集合 l
	public boolean dominate(Collection<Vertex<T>> l){

		if(l.size()==0)return true;
		return this.neighbors.containsAll(l);
	}
	
	//判断两个顶点是否相同
    public boolean equals(Object other){
        boolean result;
        if((other == null) || (getClass() != other.getClass()))
            result = false;
        else
        {
            Vertex<T> otherVertex = (Vertex<T>)other;
            result = id.equals(otherVertex.id);//节点是否相同最终还是由标识 节点类型的类的equals() 决定
        }
        return result;
    }
	
	public String toString(){
		//return this.id+"("+this.x+","+this.y+")";
		return (String) this.id+(black?"b":"w");
	}

	
	 /**
     * 利用串行化深克隆一个对象，把对象以及它的引用读到流里，在写入其他的对象
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
 /*   public Object deepClone() {
        //将对象写到流里
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(this);
			//从流里读回来
			ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bis);
			return ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }*/
}
