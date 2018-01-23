package graph;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class Graph<T> implements Serializable{
	
	private Map<T, Vertex<T>> vertices;//map 对象用来保存图中的所有顶点.T 是顶点标识
	private int edgeCount;
	private int range;
	
    public Graph(){
    	vertices = new LinkedHashMap<>();
    	edgeCount = 0;
    }
    public Graph(int range){
    	vertices = new LinkedHashMap<>();
    	edgeCount = 0;
    	this.range = range;
    }
    public Map<T, Vertex<T>> getVertices() {
		return vertices;
	}

	public void setVertices(Map<T, Vertex<T>> vertices) {
		this.vertices = vertices;
	}

	public int getEdgeCount() {
		return edgeCount;
	}

	public void setEdgeCount(int edgeCount) {
		this.edgeCount = edgeCount;
	}

	public int getRange() {
		return range;
	}
	public void setRange(int range) {
		this.range = range;
	}
    

    
/*    public void addVertex(T vertexId) {
    	vertices.put(vertexId, new Vertex<T>(vertexId));
    }*/
    
	//添加点
    public boolean addVertex(Vertex<T> vertex) {
    	if(vertices.keySet().contains(vertex.getId()))
    		return false;
    	vertices.put(vertex.getId(), vertex);
    	return true;
    }
    
	//添加点
    public Vertex<T> addRadomVertex(Vertex<T> vertex) {
    	Random r = new Random();
		int x = 0;
		int y = 0;
		while(true){
			x = r.nextInt(range);
			y = r.nextInt(range);
			vertex.setX(x);
			vertex.setY(y);
			vertex.setId((T) (x+"."+y));
			if(this.addVertex(vertex)){
				//判断添加的点坐标是否已存在
				
				return vertex;
			}
		}
    }
    
  //添加白点(rule3/rule1)
    public Vertex<T> addRadomVertex() {
    	Random r = new Random();
    	Vertex<T> vertex = new Vertex<T>(false);
		int x = 0;
		int y = 0;
		while(true){
			x = r.nextInt(range);
			y = r.nextInt(range);
			vertex.setX(-x);
			vertex.setY(-y);
			vertex.setId((T) (x+"."+y));
			if(this.addVertex(vertex)){
				//判断添加的点坐标是否已存在
				
				return vertex;
			}
		}
    }
    
    //添加边
    public boolean addEdge(T begin, T end){
    	Vertex<T> beginVertex = vertices.get(begin);
    	Vertex<T> endVertex = vertices.get(end);
    	if(beginVertex.connect(endVertex) == true){
    		edgeCount++;
    		return true;
    	}
    	return false;
    }
    
    public boolean addEdge(Vertex<T> beginVertex,Vertex<T> endVertex) {
    	if(beginVertex.connect(endVertex) == true){
    		edgeCount++;
    		return true;
    	}
    	return false;		
	}
    
    //删除边
    public void removeEdge(Vertex<T> beginVertex,Vertex<T> endVertex){
    	if(beginVertex.breakOff(endVertex))
    		edgeCount--;
    }
    
    //删除点
    public boolean removeVertex(Vertex<T> v) {
    	if(!this.vertices.values().contains(v))
    		return false;
    	
    	//手动维护链表索引
    	int size = v.getNeighbors().size();
    	for(int i = 0; i < size; i++){
    		Vertex<T> n = v.getNeighbors().get(0);
    		removeEdge(n, v);
    	}
    	this.vertices.remove(v.getId(), v);
    	return true;
    }
    
    //删除多个顶点
    public boolean removeVertices(Collection<Vertex<T>> vertices){
    	
    	if(this.vertices.values().containsAll(vertices)){
    		for (Vertex<T> vertex : vertices) {
				this.removeVertex(vertex);
			}
    		return true;
    	}
    	return false;
    }
    
    //广度优先遍历。origin 标识遍历的初始顶点
    public Queue<T> getBreadthFirstTraversal(T origin) {
		this.resetVertices();//将顶点的必要数据域初始化,复杂度为O(V)
		Queue<Vertex<T>> vertexQueue = new LinkedList<>();//保存遍历过程中遇到的顶点,它是辅助遍历的,有出队列操作
		Queue<T> traversalOrder = new LinkedList<>();//保存遍历过程中遇到的 顶点标识--整个图的遍历顺序就保存在其中,无出队操作
		Vertex<T> originVertex = vertices.get(origin);//根据顶点标识获得初始遍历顶点
		originVertex.visit();//访问该顶点
		traversalOrder.offer(originVertex.getId());
		vertexQueue.offer(originVertex);
		
		while(!vertexQueue.isEmpty()){
			Vertex<T> frontVertex = vertexQueue.poll();//出队列作为前驱点,poll()在队列为空时返回null
			List<Vertex<T>> neighbors = frontVertex.getNeighbors();
			for (Vertex<T> vertex : neighbors) {
				if(!vertex.isVisited()){
					vertex.visit();//广度优先遍历未访问的顶点
					traversalOrder.offer(vertex.getId());
					vertexQueue.offer(vertex);//将该顶点的邻接点入队列
				}
			}
			/*while(neighbors.hasNext())//对于 每个顶点都遍历了它的邻接表,即遍历了所有的边,复杂度为O(E)
			{
				Vertex<T> nextNeighbor = neighbors.next();
				if(!nextNeighbor.isVisited()){
					nextNeighbor.visit();//广度优先遍历未访问的顶点
					traversalOrder.offer(nextNeighbor.getLabel());
					vertexQueue.offer(nextNeighbor);//将该顶点的邻接点入队列
				}
			}//end inner while*/
		}//end outer while
		return traversalOrder;
	}
    
    
    //重置顶点visited信息
    private void resetVertices() {
		for (Entry<T, Vertex<T>> entry : this.vertices.entrySet()) {
			entry.getValue().setVisited(false);
		}
		
	}
	//判断连通性
    public boolean isConnected(){
    	if(this.vertices.size() == 0)
    		return false;
    	
    	if(this.edgeCount < this.vertices.size()-1){
    		//一个无向图 G=(V,E) 是连通的，边的数目大于等于顶点的数目减一：|E|>=|V|-1
    		return false;
    	}
    	Queue<T> traversalOrder = this.getBreadthFirstTraversal(this.vertices.keySet().iterator().next());
    	return (traversalOrder.size()==this.vertices.size());
    	
    }
    
    //N1(v)
    public List<Vertex<T>> neighbors1(Vertex<T> vertex){
    	List<Vertex<T>> neighbors = vertex.getNeighbors();
    	List<Vertex<T>> neighbors1 = new ArrayList<>();

    	for (Vertex<T> n : neighbors) {
			for(Vertex<T> n2 :n.getNeighbors()){
				if(n2.equals(vertex))
					continue;
				if(!neighbors.contains(n2)){//v的邻居中存在非vertex的邻居，则v符合N1(v)
					neighbors1.add(n); 
					break;	
				}
			}
		}
    	
    	return neighbors1;
    }
    
    //N2(v)
    public List<Vertex<T>> neighbors2(Vertex<T> vertex,List<Vertex<T>> neighbors1){
    	List<Vertex<T>> neighbors = vertex.getNeighbors();
    	//List<Vertex<T>> neighbors1 = this.neighbors1(vertex);
    	List<Vertex<T>> neighbors2 = new ArrayList<>();
    	for(Vertex<T> n : neighbors){
    		if(neighbors1.contains(n)){
    			continue;
    		}
    		for (Vertex<T> n2 : n.getNeighbors()) {
    			
    			if(neighbors1.contains(n2)){
    				neighbors2.add(n);
    				break;
    			}
    		}
    	}
    	return neighbors2;
    }
    
    //N3(v)
    public List<Vertex<T>> neighbors3(Vertex<T> vertex, List<Vertex<T>> neighbors1, List<Vertex<T>> neighbors2){
    	List<Vertex<T>> neighbors = vertex.getNeighbors();
    	List<Vertex<T>> neighbors3 = new ArrayList<>();
    	for (Vertex<T> n : neighbors) {
			if(neighbors1.contains(n)||neighbors2.contains(n)){
				continue;
			}else
				neighbors3.add(n);
		}
    	return neighbors3;
    }
    
    //N3(v)
    public List<Vertex<T>> neighbors3(Vertex<T> vertex, List<Vertex<T>> neighbors1){
    	List<Vertex<T>> neighbors = vertex.getNeighbors();
    	List<Vertex<T>> neighbors2 = this.neighbors2(vertex, neighbors1);
    	List<Vertex<T>> neighbors3 = new ArrayList<>();
    	for (Vertex<T> n : neighbors) {
			if(neighbors1.contains(n)||neighbors2.contains(n)){
				continue;
			}else
				neighbors3.add(n);
		}
    	return neighbors3;
    }
    
    private Set<Vertex<T>> neighbour(Vertex<T> v, Vertex<T> w){
    	Set<Vertex<T>> nvw = new HashSet<>();
    	nvw.addAll(v.getNeighbors());
    	nvw.addAll(w.getNeighbors());
    	nvw.remove(v);
    	nvw.remove(w);
    	return nvw;
    }
    
    //N1(v, w) 
    public List<Vertex<T>> neighbour1(Vertex<T> v, Vertex<T> w){
    	List<Vertex<T>> n1 = new ArrayList<>();
    	for (Vertex<T> u : this.neighbour(v, w)) {//遍历n(v,w) 
    		List<Vertex<T>> l = new ArrayList<>();
    		l.addAll(u.getNeighbors());//某顶点u的邻居集合l
    		l.removeAll(this.neighbour(v, w));// l 除去n(v,w)
			l.remove(v);//再除去v,w
			l.remove(w);
			if(l.size()>0)// size依然不为0
				n1.add(u);//则添加u到n1
		}
    	return n1;
    	
    }
    //N2(v, w) 
    public List<Vertex<T>> neighbour2(Vertex<T> v, Vertex<T> w, List<Vertex<T>> n1){
    	
    	List<Vertex<T>> n2 = new ArrayList<>();
    	List<Vertex<T>> l = new ArrayList<>();
    	l.addAll(neighbour(v, w));
    	l.removeAll(n1);//l = N(v, w) \N1(v, w)
    	for (Vertex<T> u : l) {//u∈l
			for (Vertex<T> nu : u.getNeighbors()) {//遍历u的邻居
				if(n1.contains(nu)){//存在u的邻居包含于N1，即N(u)∩N1(v, w) ≠ø
					n2.add(u);
					break;
				}
			}
		}
    	return n2;
    	
    }
    
    //N3(v, w) 
    public List<Vertex<T>> neighbour3(Vertex<T> v, Vertex<T> w, List<Vertex<T>> n1, List<Vertex<T>> n2){
    	Set<Vertex<T>> nvw = this.neighbour(v, w);
    	List<Vertex<T>> n3 = new ArrayList<>();
    	n3.addAll(nvw);//获取n(v,w)
    	n3.removeAll(n1);//除去n1(v,w)
    	n3.removeAll(n2);//除去n2(v,w)
    	return n3;
    }
    
    //获取黑点
    public List<Vertex<T>> getBlacks(){
    	Iterator<Vertex<T>> it = this.vertices.values().iterator();
    	List<Vertex<T>> blacks = new ArrayList<>();
    	while(it.hasNext()){
    		Vertex<T> v = it.next();
    		if(v.isBlack()){
    			blacks.add(v);
    		}
    	}
    	return blacks;
    }
    
    //获取相邻黑点
    public List<Set<Vertex<T>>> getNeighbourBlacks(){
    	List<Vertex<T>> blacks = this.getBlacks();
    	List<Set<Vertex<T>>> nb = new ArrayList<>();
    	for (Vertex<T> v : blacks) {
			for (Vertex<T> n : v.getNeighbors()) {
				if(n.isBlack()){
					Set<Vertex<T>> blackPairs = new HashSet<Vertex<T>>();
					blackPairs.add(v);
					blackPairs.add(n);
					if(!nb.contains(blackPairs))
						nb.add(blackPairs);
				}
			}
		}
    	
		return nb;
    	
    }
    
    //规则1
    public boolean rule1(){
    	boolean runTag = false;//表示规则有被执行的标志
    	while(true){
    		boolean flag = false;
    		
    		List<Vertex<T>> blacks = this.getBlacks();
    		List<Vertex<T>> n1 = null;
    		List<Vertex<T>> n2 = null;
    		List<Vertex<T>> n3 = null;
    		boolean blackTag;
    		for (Vertex<T> v : blacks) {
    			n1 = this.neighbors1(v);
    			n2 = this.neighbors2(v, n1);
    			n3 = this.neighbors3(v, n1, n2);
        		//忽略所有规则中加入的白点(x,y为负)
  
        		for (int i=0;i<n3.size(); i++) {
					if(n3.get(i).getX()<0 || n3.get(i).getY()<0){
						n3.remove(i);
						i --;
					}
				}
    			if(n3.size()>0 ){//N3(v) ≠ ø 
    				
    				blackTag = false;
    				for (Vertex<T> b : n1) {
    					if(b.isBlack()){
    						blackTag = true;
    						break;
    					}
    				}
     				//N1(v)中存在黑点
    				if(blackTag == true){
    					
    					//删除N2(v)∪N3(v)上的所有点，N2、N3没有交集
    					this.removeVertices(n2);
    					this.removeVertices(n3);
    					//并增加一个白点v' 和一条边(v,v' )到G中。
    					Vertex<T> w = this.addRadomVertex();
    					this.addEdge(v, w);
    					
    					flag = true;
    					runTag = true;
    					break;
    				}
    			}
    			
    		}
    		if(flag==false)break;
    	}
    	return runTag; 
    }
    
    //规则2
    public boolean rule2(){
    	
		boolean runTag = false;
		List<Vertex<T>> blacks = this.getBlacks();
		List<Vertex<T>> n1 = null;
		List<Vertex<T>> n2 = null;
		List<Vertex<T>> n3 = null;
		boolean blackTag;
		for (Vertex<T> v : blacks) {
			if (v.isBlack() == false)
				continue;
			n1 = this.neighbors1(v);
			n2 = this.neighbors2(v, n1);
			n3 = this.neighbors3(v, n1, n2);
			blackTag = false;
			for (Vertex<T> b : n1) {
				if (b.isBlack()) {
					blackTag = true;
					break;
				}
			}
			// N1(v)中存在黑点
			if (blackTag == true) {
				
				Set<Vertex<T>> n2n3 = this.union(n2, n3);
				for (Vertex<T> x : this.vertices.values()) {
					if (n2n3.contains(x) && x.isBlack() == true) {
						// x在N2或N3中且x为黑点
						runTag = true;
						x.setWhite();// 把x着为白色
						for (int i = 0; i < x.getNeighbors().size(); i++) {
							if (x.getNeighbors().get(i).isWhite() == true) {// 删除x与其白色邻居的边
								this.removeEdge(x.getNeighbors().get(i), x);
								i--;

							}
						}
					}
				}

			}
		}
    	return runTag;	
    	
    }
    
    //求交集
    private Set<Vertex<T>> intersection(Collection<Vertex<T>> a,Collection<Vertex<T>> b){
    	Set<Vertex<T>> result = new HashSet<>();
    	for (Vertex<T> v : a) {
			if(b.contains(a)){
				result.add(v);
			}
		}
    	return result;
    }
    //求并集
    private Set<Vertex<T>> union(Collection<Vertex<T>> a,Collection<Vertex<T>> b){
    	Set<Vertex<T>> result = new HashSet<>();
    	result.addAll(a);
    	result.addAll(b);
    	return result;
    }
 
/*    //规则3
    public void rule3(){
    	List<Set<Vertex<T>>> nb = this.getNeighbourBlacks();
    	Vertex<T> v = null;
    	Vertex<T> w = null;
    	List<Vertex<T>> n1 = null;
    	List<Vertex<T>> n2 = null;
    	List<Vertex<T>> n3 = null;
    	Set<Vertex<T>> B = null;
    	for (Set<Vertex<T>> pairs : nb) {
    		List<Vertex<T>> l = new ArrayList<>(pairs);
    		v = l.get(0);
    		w = l.get(1);
    		if(!this.vertices.containsValue(v) || !this.vertices.containsValue(w)){
    			//先判断该黑点对是不是都在图中。因前面循环可能会从图中删除其中某些黑点导致本黑点对不存在。
    			continue;
    		}
    		n1 = this.neighbour1(v, w);
    		n2 = this.neighbour2(v, w, n1);
    		n3 = this.neighbour3(v, w, n1, n2);
    		Set<Vertex<T>> n2n3 = this.union(n3, n2);
    		B = this.union(n3,this.intersection(n2, this.intersection(v.getNeighbors(), w.getNeighbors())));
    		boolean endTag = false;
    		for (Vertex<T> vertex : n2n3) {//N2(v, w)∪N3(v, w)中任何一个点均不能支配N3(v, w)
				if(vertex.dominate(n3)){
					endTag = true;
					break;
				}
			}
    		if(!endTag){
    			//......
    			if(v.dominate(n3) && w.dominate(n3)){
    				//（1）v和w均能支配N3(v, w)则从G 中删除B上顶点，增加一个白点z 及两条边(v, z)、(w, z)到G 中；
    				for (Vertex<T> vertex : B) {
						this.vertices.remove(vertex.getId(), vertex);
					}
    				Vertex<T> z = this.addRadomVertex(new Vertex<T>(false));
    				this.addEdge(v, z);
    				this.addEdge(w, z);
    				
    			}else if(v.dominate(n3) && !w.dominate(n3)){
    				//(2)v能支配N3(v, w)，但w不能支配N3(v, w)，则从G 中删除B上顶点，增加一个白点v'及一条边(v,v')到G中
    				for (Vertex<T> vertex : B) {
						this.vertices.remove(vertex.getId(), vertex);
					}
    				Vertex<T> z = this.addRadomVertex(new Vertex<T>(false));
    				this.addEdge(v, z);
    				
    			}else if(w.dominate(n3) && !v.dominate(n3)){
    				//(3)w能支配N3(v, w)，但v不能支配N3(v, w)，则从G 中删除B上顶点，增加一个白点w'及一条边(w, w' )到G中
       				for (Vertex<T> vertex : B) {
    						this.vertices.remove(vertex.getId(), vertex);
    				}
       				Vertex<T> z = this.addRadomVertex(new Vertex<T>(false));
       				this.addEdge(w, z);
    				
    			}else if(!v.dominate(n3) && !w.dominate(n3)){
    				//(4)v和w均不能支配N3(v, w)，则从G 中删除B上顶点，增加两个点v'、w'及两条边(v,v' )、(w, w')到G中
       				for (Vertex<T> vertex : B) {
    						this.vertices.remove(vertex.getId(), vertex);
    				}
       				Vertex<T> v_ = this.addRadomVertex(new Vertex<T>(false));
       				Vertex<T> w_ = this.addRadomVertex(new Vertex<T>(false));
       				this.addEdge(v, v_);
       				this.addEdge(w, w_);
    			}
    		}
		}
    
    }*/
    
    
    public boolean rule3(){
    	boolean runTag = false;
    	while(true){
    		
    		List<Set<Vertex<T>>> nb = this.getNeighbourBlacks();
    		boolean flag = false;
    		
    		for(Set<Vertex<T>> pairs : nb){
    			
    			List<Vertex<T>> l = new ArrayList<>(pairs);
        		Vertex<T> v = l.get(0);
        		Vertex<T> w = l.get(1);
        		List<Vertex<T>> n1 = this.neighbour1(v, w);
        		List<Vertex<T>> n2 = this.neighbour2(v, w, n1);
        		List<Vertex<T>> n3 = this.neighbour3(v, w, n1, n2);
        		//重新计算n1n2n3时须忽略前面循环中执行rule3时加入的点(x,y为负的点)
/*        		for (int i=0;i<n1.size(); i++) {
					if(n1.get(i).getX()<0 || n1.get(i).getY()<0){
						n1.remove(i);
						i --;
					}
				}
        		for (int i=0;i<n2.size(); i++) {
					if(n2.get(i).getX()<0 || n2.get(i).getY()<0){
						n2.remove(i);
						i --;
					}
				}*///N1N2不会有新增的白点 新增的白点都在N3里
        		for (int i=0;i<n3.size(); i++) {
					if(n3.get(i).getX()<0 || n3.get(i).getY()<0){
						n3.remove(i);
						i --;
					}
				}
        		
        		Set<Vertex<T>> n2n3 = this.union(n3, n2);
        		Set<Vertex<T>> B = this.union(n3,this.intersection(n2, this.intersection(v.getNeighbors(), w.getNeighbors())));
        	
        		boolean endTag = false;
        		if(n3.size()==0||n3==null){
        			endTag = true;
        		}
        		for (Vertex<T> vertex : n2n3) {//N2(v, w)∪N3(v, w)中任何一个点均不能支配N3(v, w)
        			if(vertex.dominate(n3)){
    					endTag = true;
    					break;
    				}
    			}
        		
        		if(!endTag){
        			//规则三执行条件符合 
        			if(v.dominate(n3) && w.dominate(n3)){
        				//（1）v和w均能支配N3(v, w)则从G 中删除B上顶点，增加一个白点z 及两条边(v, z)、(w, z)到G 中；
/*        				for (Vertex<T> vertex : B) {
    						this.removeVertex(vertex);
    					}*/
        				this.removeVertices(B);
        				Vertex<T> z = this.addRadomVertex();
        				this.addEdge(v, z);
        				this.addEdge(w, z);

        				
        				
        			}else if(v.dominate(n3) && !w.dominate(n3)){
        				//(2)v能支配N3(v, w)，但w不能支配N3(v, w)，则从G 中删除B上顶点，增加一个白点v'及一条边(v,v')到G中
/*        				for (Vertex<T> vertex : B) {
    						this.removeVertex(vertex);
    					}*/
        				this.removeVertices(B);
        				Vertex<T> z = this.addRadomVertex();
        				this.addEdge(v, z);


        				
        			}else if(w.dominate(n3) && !v.dominate(n3)){
        				//(3)w能支配N3(v, w)，但v不能支配N3(v, w)，则从G 中删除B上顶点，增加一个白点w'及一条边(w, w' )到G中
 /*          				for (Vertex<T> vertex : B) {
        						this.removeVertex(vertex);
        				}*/
        				this.removeVertices(B);
           				Vertex<T> z = this.addRadomVertex();
           				this.addEdge(w, z);


        				
        			}else if(!v.dominate(n3) && !w.dominate(n3)){
        				//(4)v和w均不能支配N3(v, w)，则从G 中删除B上顶点，增加两个点v'、w'及两条边(v,v' )、(w, w')到G中
/*           				for (Vertex<T> vertex : B) {
        						this.removeVertex(vertex);
        				}*/
        				this.removeVertices(B);
           				Vertex<T> v_ = this.addRadomVertex();
           				Vertex<T> w_ = this.addRadomVertex();
           				this.addEdge(v, v_);
           				this.addEdge(w, w_);


        			}
        			flag = true;
        			runTag = true;
        			break;
        		}
    		}
    		if(flag == false)break;

    	}
    	return runTag;
    }

    //规则4
    public boolean rule4(){
    	boolean runTag = false;
    	
    	List<Set<Vertex<T>>> nb = this.getNeighbourBlacks();
    	for (Set<Vertex<T>> pairs : nb) {//对于图G 上相邻黑点v、w
    		
			List<Vertex<T>> l = new ArrayList<>(pairs);
    		Vertex<T> v = l.get(0);
    		Vertex<T> w = l.get(1);
    		
    		if(v.isWhite()||w.isWhite())continue;//排除在本次规则执行时变白的点
    		
    		List<Vertex<T>> n1 = this.neighbour1(v, w);
    		List<Vertex<T>> n2 = this.neighbour2(v, w, n1);
    		List<Vertex<T>> n3 = this.neighbour3(v, w, n1, n2);
       		for (int i=0;i<n3.size(); i++) {
					if(n3.get(i).getX()<0 || n3.get(i).getY()<0){
						n3.remove(i);
						i --;
					}
				}
    		
    		Set<Vertex<T>> n2n3 = this.union(n3, n2);
    		
    		if(n3.size()>0){//如果N3(v, w)≠ ø
    			
    			for (Vertex<T> x : n2n3) {
    				if(x.isWhite())continue;//对每一个黑点x属于n2Un3
    				if(!x.dominate(n3)){//如果x不能支配N3(v, w)
    					runTag = true;
    					x.setWhite();//将x着为白色并删除x与其它白点间的边。
    					for (int i=0;i< x.getNeighbors().size();i++) {
    						if(x.getNeighbors().get(i).isWhite()){
    							this.removeEdge(x, x.getNeighbors().get(i));
    							i--;
    						}
    					}
    				}
    			}
    		}
    		
		}
    	return runTag;
    }
    
    //获取相邻点
    public Set<Set<Vertex<T>>> getNeighborPairs(){
    	Collection<Vertex<T>> vertices = this.vertices.values();
    	Set<Set<Vertex<T>>> neighborPairs = new HashSet<>();
    	
    	for (Vertex<T> v : vertices) {
			for (Vertex<T> n : v.getNeighbors()) {
				Set<Vertex<T>> pairs = new HashSet<Vertex<T>>();
				pairs.add(v);
				pairs.add(n);
				neighborPairs.add(pairs);
			}
		}
    	return neighborPairs;
    }
    
    //规则5
    public boolean rule5(){
    	boolean runTag = false;
    	while(true){
    		boolean flag = false;
    		Set<Set<Vertex<T>>> np = this.getNeighborPairs();
    		for (Set<Vertex<T>> pairs : np) {//对于G 上相邻点v、w
    			List<Vertex<T>> l = new ArrayList<>(pairs);
    			Vertex<T> v = l.get(0);
    			Vertex<T> w = l.get(1);
    			List<Vertex<T>> nv = v.getNeighbors();
    			List<Vertex<T>> nw = w.getNeighbors();
    			
    			boolean endTag = true;//Nv∩Nw中存在黑点
    			for( Vertex<T> b : this.intersection(nv, nw)){
    				if(b.isBlack()){
    					endTag = false;
    					break;
    				}
    			}
    			if(endTag == true)continue;
    			
    			nv.add(v);
    			nw.add(w);
    			if(!nv.equals(nw))continue;
    			//N[v]=N[w]
    			if(v.isWhite() && w.isBlack()){
    				this.removeVertex(v);
    			}else if(v.isBlack() && w.isWhite()){
    				this.removeVertex(w);
    			}else{
    				this.removeVertex(v);
    			}
    			flag = true;
    			runTag = true;
    			break;
    		}
    		if(flag == false)break;
		}
    	return runTag;
    }
    
    
    public Set<Vertex<T>> getWhites(){
    	Collection<Vertex<T>> vertices = this.vertices.values();
    	//获取图中白点集
    	Set<Vertex<T>> whites = new HashSet<>();
    	for (Vertex<T> v : vertices) {
			if(v.isWhite()){
				whites.add(v);
			}
		}
    	return whites;
    }
    
    //获取两白点
    public Set<Set<Vertex<T>>> getWhitePairs(){
    	Set<Set<Vertex<T>>> whitePairs = new HashSet<>();
    	
    	//获取图中白点集
    	Set<Vertex<T>> whites = this.getWhites();
    	
    	//获取白点对
    	for (Vertex<T> u : whites) {
			for (Vertex<T> v : whites) {
				if(u.equals(v))continue;
				Set<Vertex<T>> pairs = new HashSet<>();
				pairs.add(u);
				pairs.add(v);
				
				whitePairs.add(pairs);
			}
		}
    	return whitePairs;
    	
    }
    
    //规则6
    public boolean rule6(){
    	boolean runTag = false;
    	while(true){
    		boolean flag = false;
    		
    		Set<Set<Vertex<T>>> wp = this.getWhitePairs();
    		for (Set<Vertex<T>> pairs : wp) {
    			List<Vertex<T>> l = new ArrayList<>(pairs);
    			Vertex<T> u = l.get(0);
    			Vertex<T> v = l.get(1);
    			if(v.getNeighbors().containsAll(u.getNeighbors())){
    				this.removeVertex(v);
    				flag = true;
    				runTag = true;
    				break;  
    			}
    		}
    		if(flag == false)break;
    	}
    	return runTag;
    	
    }
    
    //G上一对点(黑点v和白点w)
    public Set<Set<Vertex<T>>> getBlackWhitePairs(){
    	Set<Set<Vertex<T>>> blackWhitePairs = new HashSet<>();
    	
    	//
    	List<Vertex<T>> blacks = this.getBlacks();
    	Set<Vertex<T>> whites = this.getWhites();
    	
    	for (Vertex<T> b : blacks) {
			for (Vertex<T> w : whites) {
				Set<Vertex<T>> pairs = new HashSet<>();
				pairs.add(b);
				pairs.add(w);
				blackWhitePairs.add(pairs);
			}
		}
    	return blackWhitePairs;
    }
    
    //规则7
    public boolean rule7(){
    	boolean runTag = false;
    	while(true){
    		boolean flag = false;
    		
    		Set<Set<Vertex<T>>> bwp = this.getBlackWhitePairs();
    		for (Set<Vertex<T>> pairs : bwp) {
    			Vertex<T> v = null;
    			Vertex<T> w = null;
    			for (Vertex<T> vertex : pairs) {
    				if(vertex.isBlack()){
    					v = vertex; 
    				}else{
    					w = vertex;
    				}
    			}
    			
    			boolean endTag = false;//如果w与N(v)中的每个黑点均相邻
    			for (Vertex<T> vertex : v.getNeighbors()) {
    				if(vertex.isBlack()){
    					if(!vertex.getNeighbors().contains(w)){
    						//若Nv中有黑点与w不相邻，则结束
    						endTag = true;
    						break;
    					}
    				}
    			}
    			if(!endTag){
    				this.removeVertex(w);//将w从图G中删除
    				flag = true;
    				runTag = true;
    				break;
    			}
    		}
    		if(flag == false)break;
    	}
    	return runTag;
    }
    
    public void print(){
    	List<T> keySet = new ArrayList<T>(this.vertices.keySet());
    	
    	for (T t : keySet) {
    		List<Vertex<T>> neighbours = this.vertices.get(t).getNeighbors();

			System.out.println(t+"("+this.vertices.get(t).getX()+","+this.vertices.get(t).getY()+"---"+neighbours);
		}
    }
    
	//输出到txt
    public void write(String path){
    	FileWriter fw;
		try {
			fw = new FileWriter(path);
			fw.write(this.toString());
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    
    public String toString(){
    	List<T> keySet = new ArrayList<T>(this.vertices.keySet());
		String adjList = "";
		adjList = "邻接表："+"\r\n";
    	for (T t : keySet) {
    		List<Vertex<T>> neighbours = this.vertices.get(t).getNeighbors();
    		adjList = adjList+this.vertices.get(t)+"("+this.vertices.get(t).getX()+","+this.vertices.get(t).getY()+") --- "+neighbours+"  \r\n";
			
		}
    	return adjList;
    }

	
	@Override  
    public Object clone() throws CloneNotSupportedException  
    {  
        return super.clone();  
    } 
	
	 /**
     * 利用串行化深克隆一个对象，把对象以及它的引用读到流里，在写入其他的对象
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Object deepClone() throws IOException,ClassNotFoundException {
        //将对象写到流里
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(this);
        //从流里读回来
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        return ois.readObject();
    }
    
  /*  @SuppressWarnings("unchecked")
	public List<Vertex<T>> deepCopy(List<Vertex<T>> vertices2)  {  
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();  
        ObjectOutputStream out;
        List<Vertex<T>> dest = null;
		try {
			out = new ObjectOutputStream(byteOut);
			out.writeObject(vertices2);  
			ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());  
			ObjectInputStream in = new ObjectInputStream(byteIn);  
			
			dest = (List<Vertex<T>>) in.readObject();  
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
      
        return dest;  
    }  */
    
    
}
