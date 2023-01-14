package FibonacciHeapPackage;

import java.util.Arrays;

/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
public class FibonacciHeap
{
	private HeapNode min = null;
	public HeapNode first = null;
	private int heapSize = 0; //Total number of nodes
	private int numOfMarked =0; //The number of nodes which are marked
	private int numOfTrees = 0; //The total number of trees
	private static int numOfLinks = 0; //The total number of times we merged 2 trees of the same degree
	private static int numOfCuts = 0; //The total number of node cutting that was performed
	
   /**
    * public boolean isEmpty()
    *
    * Returns true if and only if the heap is empty.
    *   
    */
    public boolean isEmpty()
    {
    	return first == null; // should be replaced by student code
    }
    
    public int getNumTrees()
    {
    	return this.numOfTrees;
    }
    
    public void printHeap()
    {
    	if(this.first==null)
    	{
    		System.out.println(Arrays.toString(new String[]{}));
    		return;
    	}
    	HeapNode tmpRoot = this.first;
    	do
    	{
    		HeapNode[] layer = new HeapNode[1];
    		layer[0] = tmpRoot;
    		int[] firstPrint = {tmpRoot.getKey()};
    		System.out.println(Arrays.toString(firstPrint));
    		while(layer.length>0)
    		{
    			int nextLayerLen = 0;
    			for(int i=0; i<layer.length; i++)
    			{
    				String[] nodeChildren = new String[layer[i].getDegree()];
    				HeapNode tmpChild = layer[i].getChild();
    				for(int j=0; j<nodeChildren.length; j++)
    				{
    					String tmpKey = String.valueOf(tmpChild.getKey());
    					if(tmpChild.isNodeMarked())
    					{
    						tmpKey += "*";
    					}
    					nodeChildren[j] = tmpKey;
    					tmpChild = tmpChild.getNext();
    				}
    				System.out.print(Arrays.toString(nodeChildren));
    				nextLayerLen += nodeChildren.length;
    			}
    			HeapNode[] nextLayer = new HeapNode[nextLayerLen];
    			
    			int tmpIndex = 0;
    			for(int i=0; i<layer.length; i++)
    			{
    				int d = layer[i].getDegree();
    				HeapNode tmpChild = layer[i].getChild();
    				for(int j=0; j<d; j++)
    				{
    					nextLayer[tmpIndex] = tmpChild;
    					tmpChild = tmpChild.getNext();
    					tmpIndex++;
    				}
    			}
    			layer = nextLayer;
    			System.out.println("");
    		}
    		tmpRoot = tmpRoot.getNext();
    	}while(tmpRoot!=this.first);
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    * The added key is assumed not to already belong to the heap.  
    * 
    * Returns the newly created node.
    */
    public HeapNode insert(int key)
    {
    	//Update size
    	this.heapSize += 1;
    	this.numOfTrees += 1;
    	
    	
    	//The new node
    	HeapNode newNode = new HeapNode(key);
    	
    	//Extreme case
    	if(this.heapSize==1)
    	{
    		this.first = newNode;
    		this.min = newNode;
    		newNode.setNext(newNode);
    		newNode.setPrev(newNode);
    		return newNode;
    	}
    	
    	//Set pointers from newNode
    	newNode.setNext(this.first);
    	newNode.setPrev(this.first.getPrev());
    	
    	//Set pointers to newNode
    	newNode.getNext().setPrev(newNode);
    	newNode.getPrev().setNext(newNode);
    	
    	//Now newNode is first
    	this.first = newNode;
    	
    	//Update min if needed
    	if(key < this.min.getKey())
    	{
    		this.min = newNode;
    	}
    	return newNode; // should be replaced by student code
    }

   /**
    * public void deleteMin()
    *
    * Deletes the node containing the minimum key.
    *
    */
    public void deleteMin()
    {
     	this.delete(this.min);
     	
    }
    
    public void consolidate()
    {
    	//Case the FibonacciHeap is now empty
    	if(this.isEmpty())
    	{
    		return;
    	}
    	//Here we will eventually have the new minimum node in the data structure
    	HeapNode newMin = this.first;
    	
    	double phi = (1 + Math.sqrt(5))/2;
    	double arrLen = Math.log(this.heapSize)/Math.log(phi);
    	HeapNode[] heapArr = new HeapNode[(int) arrLen + 1];
    	
    	for(int i =0; i<heapArr.length; i++)
    	{
    		heapArr[i] = null;
    	}
    	HeapNode tmp = this.first;
    	while(true)
    	{
    		if(tmp.getKey() < newMin.getKey())
    		{
    			newMin = tmp;
    		}
    		int d =tmp.getDegree();
    		if(heapArr[d]==null)
    		{
    			heapArr[d] = tmp;
    			tmp = tmp.getNext();
    		}
    		
    		else
    		{
    			if(tmp==heapArr[d]) //Stop condition
    			{
    				break;
    			}
    			tmp = this.mergeNodes(heapArr[d], tmp);
    			heapArr[d] = null;
    		}
    	}
    	this.min = newMin;
    	
    	for(int i=0; i<heapArr.length; i++)
    	{
    		if(heapArr[i]!=null)
    		{
    			this.first = heapArr[i];
    			break;
    		}
    	}
    }
    
    //Receives 2 roots from the FibonacciHeap
    //Merges the two together so that one root is the parent of the other 
    private HeapNode mergeNodes(HeapNode node1, HeapNode node2)
    {
    	//Because we performed another linking 
    	FibonacciHeap.numOfLinks += 1;
    	
    	//Total number of trees decreases by 1
    	this.numOfTrees-=1;
    	
    	// bigNode equals the one of node1, node2 who has the greater key
    	//smallNode is the other one
    	//We are going to make bigNode a new son of smallNode
    	HeapNode bigNode;
    	HeapNode smallNode;
    	if(node1.getKey() > node2.getKey())
    	{
    		bigNode = node1;
    		smallNode = node2;
    	}
    	else
    	{
    		bigNode = node2;
    		smallNode = node1;
    		
    	}
    	
    	
		
		//Update degree
		smallNode.setDegree(smallNode.getDegree() + 1);
		
		//Get bigNode out of the linked list of the roots
		bigNode.getNext().setPrev(bigNode.getPrev());
		bigNode.getPrev().setNext(bigNode.getNext());
		
    	//Extreme case
    	if(smallNode.child == null)
    	{
        	smallNode.setChild(bigNode);
    		bigNode.setParent(smallNode);
    		bigNode.setNext(bigNode);
    		bigNode.setPrev(bigNode);
    		return smallNode;
    	}
    	
    	//Now assuming smallNode.child isn't null...
    	//Set pointers of bigNode
    	bigNode.setParent(smallNode);
    	bigNode.setNext(smallNode.getChild());
    	bigNode.setPrev(smallNode.getChild().getPrev());
    	
    	//Set the pointers to bigNode
    	bigNode.getNext().setPrev(bigNode);
    	bigNode.getPrev().setNext(bigNode);
    	smallNode.setChild(bigNode);
    	return smallNode;
    }

   /**
    * public HeapNode findMin()
    *
    * Returns the node of the heap whose key is minimal, or null if the heap is empty.
    *
    */
    public HeapNode findMin()
    {
    	return this.min;// should be replaced by student code
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Melds heap2 with the current heap.
    *
    */
    public void meld (FibonacciHeap heap2)
    {
    	this.numOfMarked += heap2.size() - heap2.nonMarked();
    	this.numOfTrees += heap2.getNumTrees();
    	//Update size
    	this.heapSize += heap2.size();
    	
    	//Set pointers from heap2
    	heap2.first.getPrev().setNext(this.first);
    	heap2.first.setPrev(this.first.getPrev());
    	
    	//Set pointers from this
    	this.first.getPrev().setNext(heap2.first);
    	this.first.setPrev(heap2.first.getPrev());
    	
    	//Update min if needed
    	if(this.min.getKey() > heap2.findMin().getKey())
    	{
    		this.min = heap2.findMin();
    	}
    }

   /**
    * public int size()
    *
    * Returns the number of elements in the heap.
    *   
    */
    public int size()
    {
    	return this.heapSize; // should be replaced by student code
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
    * (Note: The size of of the array depends on the maximum order of a tree.)  
    * 
    */
    public int[] countersRep()
    {
    	double phi = (1 + Math.sqrt(5))/2;
    	double arrLen = Math.log(this.heapSize)/Math.log(phi);
    	int[] heapDegrees = new int[(int) arrLen + 1];
    	
    	HeapNode tmpNode = this.first;
    	do
    	{
    		heapDegrees[tmpNode.getDegree()] += 1;
    		tmpNode = tmpNode.getNext();
    	}while(tmpNode!=this.first);
    	
    	int outputLen = 0;
    	for(int i=0; i<heapDegrees.length; i++)
    	{
    		if(heapDegrees[i]>0)
    		{
    			outputLen = i+1;
    		}
    	}
    	
    	int[] output = new int[outputLen];
    	for(int i=0; i<output.length; i++)
    	{
    		output[i] = heapDegrees[i];
    	}
        return output; //	 to be replaced by student code
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap.
	* It is assumed that x indeed belongs to the heap.
    *
    */
    public void delete(HeapNode x) 
    {
    	//Start by cutting x
    	this.cutNode(x);
    	
    	while(x.getDegree()>0)
    	{
    		this.cutNode(x.getChild());
    	}
    	
    	//Now x has no children and can be deleted with deleteLoneNode
    	this.deleteLoneNode(x);
    	
    	
    	
    	//In case we deleted the minimum we need to use consolidate
    	if(this.min.getKey() == x.getKey())
    	{
    		this.consolidate();
    	}
    }
    
    //Deletes a node with x.child==null and x.parent==null
    public void deleteLoneNode(HeapNode x)
    {
    	this.numOfTrees -= 1;
    	this.heapSize -= 1;
    	if(this.heapSize == 0)
    	{
    		this.first = null;
    		return;
    	}
    	x.getPrev().setNext(x.getNext());
    	x.getNext().setPrev(x.getPrev());
    	
    	//Case this node is first
    	if(x==this.first)
    	{
    		this.first = x.getNext();
    	}
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta)
    {
    	//Update key
    	x.key -= delta;
    	
    	//And delete
    	this.cutNode(x);
    }
    
    public void cutNode(HeapNode x)
    {
    	if(x.isRoot())
    	{//Can't cut roots
    		return;
    	}
    	
    	this.numOfCuts += 1;
    	
    	//Beacause the number of trees will increase from the action
    	this.numOfTrees += 1;
    	
    	if(x.getNext()!=null) //If x has siblings
    	{//Set pointers of x.next and x.prev
    		x.getPrev().setNext(x.getNext());
        	x.getNext().setPrev(x.getPrev());
    	}
    	//The parent
    	HeapNode xParent = x.getParent();
    	
    	if(xParent.getChild()==x)
    	{
    		if(x.getNext()!=x) //Case xParent still has children
    		{
    			xParent.setChild(x.getNext());
    		}
    		else
    		{
    			xParent.setChild(null);
    		}
    		
    	}
    	
    	//Set pointers of x
    	x.setNext(this.first);
    	x.setPrev(this.first.getPrev());
    	x.setParent(null);
    	this.first = x;
    	
    	//Set new pointers to x
    	x.getPrev().setNext(x);
    	x.getNext().setPrev(x);
    	
    	//Root is never marked so...
    	if(x.isNodeMarked())
    	{
    		x.setMarked(false);
    		numOfMarked -= 1;
    	}
    	
    	
    	//Update parent's degree
    	xParent.setDegree(xParent.getDegree()-1);
    	if(xParent.isMarked)
    	{
    		this.cutNode(xParent);
    	}
    	else
    	{
    		if(!xParent.isRoot())
    		{
    			xParent.setMarked(true);
    			numOfMarked += 1;
    		}
    	}
    }

   /**
    * public int nonMarked() 
    *
    * This function returns the current number of non-marked items in the heap
    */
    public int nonMarked() 
    {    
        return this.heapSize - this.numOfMarked; // should be replaced by student code
    }

   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * 
    * In words: The potential equals to the number of trees in the heap
    * plus twice the number of marked nodes in the heap. 
    */
    public int potential() 
    {    
        return this.numOfTrees + 2*this.nonMarked(); // should be replaced by student code
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the
    * run-time of the program. A link operation is the operation which gets as input two
    * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
    * tree which has larger value in its root under the other tree.
    */
    public static int totalLinks()
    {    
    	return FibonacciHeap.numOfLinks; // should be replaced by student code
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the
    * run-time of the program. A cut operation is the operation which disconnects a subtree
    * from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts()
    {    
    	return FibonacciHeap.numOfCuts; // should be replaced by student code
    }
    
    

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
    * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
    *  
    * ###CRITICAL### : you are NOT allowed to change H. 
    */
    public static int[] kMin(FibonacciHeap H, int k)
    {    
        int[] arr = new int[k];
        FibonacciHeap potentialMinHeap = new FibonacciHeap();
        potentialMinHeap.insert(H.findMin());
        for (int i = 0; i < k-1; i++) {
        	HeapNode currMin = potentialMinHeap.findMin();
        	arr[i] = currMin.getKey();
        	potentialMinHeap.deleteMin();
        	HeapNode childOfCurrMin = currMin.getChild();
        	HeapNode nextPointerForChild = childOfCurrMin;
        	HeapNode prevPointerForChild = childOfCurrMin;
        	while (nextPointerForChild != null) {
        		potentialMinHeap.insert(nextPointerForChild.getKey());
        		if (nextPointerForChild.getNext() == nextPointerForChild) {
        			break;
        		}
        		nextPointerForChild = nextPointerForChild.getNext();
        	}
        	while (prevPointerForChild != null) {
        		potentialMinHeap.insert(prevPointerForChild.getKey());
        		if (prevPointerForChild.getPrev() == prevPointerForChild) {
        			break;
        		}
        		prevPointerForChild = prevPointerForChild.getPrev();
        	}
        }
        return arr; // should be replaced by student code
    }
    
   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in another file. 
    *  
    */
    public static class HeapNode{

    	public int key;
    	public boolean isMarked;
    	public int degree;
    	public HeapNode next;
    	public HeapNode parent;
    	public HeapNode prev;
    	public HeapNode child;

    	public HeapNode(int key) {
    		this.key = key;
    		this.isMarked = false;
    		this.degree = 0;
    		this.next = null;
    		this.prev = null;
    		this.parent = null;
    		this.child = null;
    	}
    	
    	public boolean isRoot()
    	{
    		return this.parent==null;
    	}

    	public int getKey() {
    		return this.key;
    	}
    	
    	public boolean isNodeMarked()
    	{
    		return this.isMarked;
    	}
    	
    	public int getDegree()
    	{
    		return this.degree;
    	}
    	
    	public HeapNode getNext()
    	{
    		return this.next;
    	}
    	
    	public HeapNode getPrev()
    	{
    		return this.prev;
    	}
    	
    	public HeapNode getParent()
    	{
    		return this.parent;
    	}
    	
    	public HeapNode getChild()
    	{
    		return this.child;
    	}
    	
    	public void setNext(HeapNode node)
    	{
    		this.next = node;
    	}
    	
    	public void setPrev(HeapNode node)
    	{
    		this.prev = node;
    	}
    	
    	public void setParent(HeapNode node)
    	{
    		this.parent = node;
    	}
    	
    	public void setChild(HeapNode node)
    	{
    		this.child = node;
    	}
    	
    	public void setDegree(int d)
    	{
    		this.degree = d;
    	}
    	
    	public void setMarked(boolean marked)
    	{
    		this.isMarked = marked;
    	}
    }
}