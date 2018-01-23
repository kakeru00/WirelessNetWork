package graph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GenerateGraph {
	//生成图，参数分别为 节点坐标范围、节点数目、节点通讯半径
	private static Graph<String> generate(int range, int vertexCount, int contactRadius){
		Graph<String> g = new Graph<String>(range);
		Random r = new Random();
		int x = 0;
		int y = 0;
		//产生点
		for(int i=0; i<vertexCount; i++){
			while(true){
				x = r.nextInt(range);
				y = r.nextInt(range);
				Vertex<String> vertex = new Vertex<String>(x+"."+y,x,y);
				if(g.addVertex(vertex))//判断添加的点是否已存在
					break;
			}
			
		}
		
		//产生边
		List<Vertex<String>> vertexList = new ArrayList<Vertex<String>>( g.getVertices().values());
		int count = 1;
		System.out.println("所有顶点："+vertexList);
		for (Vertex<String> vertex : vertexList) {
			for(int i = count; i < vertexList.size(); i ++){
				Vertex<String> v = vertexList.get(i);
				//System.out.println(vertex+"——"+v+"相距:"+(int)vertex.distance(v));
				if(vertex.distance(v) < contactRadius){
					g.addEdge(vertex.getId(), v.getId());
				}
			}
			count ++;
		}
		return g;
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		int range = 50;
		int vertexCount = 25;
		int contactRadius = 15;
		Graph<String> graph = null;
		while(true){
			boolean flag = true;
			graph = generate(range,vertexCount,contactRadius);
			for (Vertex<String> v : graph.getVertices().values()) {
				if(v.getNeighbors().size()==0){
					flag = false;
					break;
				}
			}
			if(flag==true){
				System.out.println("graph complete");
				break;
			}
		}
		Set<String> keySet = graph.getVertices().keySet();
		Collection<Vertex<String>> values = graph.getVertices().values();
		
		System.out.println("范围:"+range+"x"+range+", "+"顶点数："+vertexCount+", "+"通讯半径："+contactRadius+", 边数："+graph.getEdgeCount());
		System.out.println(graph);
		Graph<String> h = (Graph<String>) graph.deepClone();
		Graph<String> h1 = (Graph<String>) graph.deepClone();
		Graph<String> h2 = (Graph<String>) graph.deepClone();
		Graph<String> h3 = (Graph<String>) graph.deepClone();
		Graph<String> h4 = (Graph<String>) graph.deepClone();
		Graph<String> h5 = (Graph<String>) graph.deepClone();
		Graph<String> h6 = (Graph<String>) graph.deepClone();
		Graph<String> h7 = (Graph<String>) graph.deepClone();
/*		System.out.println("广度优先遍历："+graph.getBreadthFirstTraversal(graph.getVertices().keySet().iterator().next()));
		
		System.out.println("连通性："+graph.isConnected());
		Vertex<String> vertex = values.iterator().next();
		List<Vertex<String>> neighbors1 = graph.neighbors1(vertex);
		List<Vertex<String>> neighbors2 = graph.neighbors2(vertex, neighbors1);
		List<Vertex<String>> neighbors3 = graph.neighbors3(vertex, neighbors1, neighbors2);
		//graph.write("d:\\graph.txt");
*/


		
		while(true){
			boolean runTag = false;
			
			if(h.rule1()){
				System.out.println("rule1 complete");
				runTag=true;
			}
			if(h.rule2()){
				System.out.println("rule2 complete");
				runTag=true;
			}
			if(h.rule3()){
				System.out.println("rule3 complete");
				runTag=true;
			}
			if(h.rule4()){
				System.out.println("rule4 complete");
				runTag=true;
			}
			if(h.rule5()){
				System.out.println("rule5 complete");
				runTag=true;
			}
			if(h.rule6()){
				System.out.println("rule6 complete");
				runTag=true;
			}
			if(h.rule7()){
				System.out.println("rule7 complete");
				runTag=true;
			}
			
			if(runTag == false)break;
		}
		
		System.out.println("完成后\n"+h);
		
	}
}
