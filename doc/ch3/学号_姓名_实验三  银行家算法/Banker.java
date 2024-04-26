import java.util.*;

public class  Banker{
public static void main(String[] args) {
   Scanner scanner = new Scanner(System.in);
   TheBanker tb = new TheBanker();
   tb.deadlockAvoidance();//死锁避免
   int gate = 1;
   while(gate!=0){ 
     tb.deadlockDetection();//死锁检测
     System.out.println("如果您要继续分配资源请输入\"1\"，退出请输入\"0\"");
     System.out.print("您输入的值为：");
     gate = scanner.nextInt();
     System.out.println();
   }
   System.out.println("使用愉快！期待您下次使用！");
}
}

class TheBanker{
int m;
int n;
int[][] max;
int[][] maxbak;//备份用
int[][] allocation;
int[][] allocationbak;//备份用
int[][] need;
int[][] needbak;//备份用
int[] available;
int[] availablebak;//备份用

public TheBanker(){  
   Scanner s = new Scanner(System.in);
   System.out.println("初始化=============="); 
   System.out.print("请依次输入系统中的【进程数】和【资源类型数】：");
   m = s.nextInt();
   n = s.nextInt();
     max =new int[m][n];
     maxbak = new int[m][n];
     allocation = new int[m][n];
     allocationbak = new int[m][n];
     need = new int[m][n];
     needbak = new int[m][n];
     available = new int[n];
     availablebak = new int[n];
     
     for(int i=0;i<max.length;i++){//初始化向量MAX、ALLOCATION、NEED、AVAILABLE
    	 System.out.print("请依次输入第" + i + "进程的各资源最大需求数：");
	     for(int j=0;j<max[i].length;j++){
	        max[i][j] = s.nextInt();
	        maxbak[i][j] = max[i][j];
	     }
     }     
     for(int i=0;i<allocation.length;i++){
    	 System.out.print("请依次输入第" + i + "进程中已分配资源的数量：");
	     for(int j=0;j<allocation[i].length;j++){
	        allocation[i][j] = s.nextInt();
	        allocationbak[i][j] = allocation[i][j];
	     }
     }
     //需求矩阵自动求出了
     for(int i=0;i<need.length;i++){
	     for(int j=0;j<need[i].length;j++){
	        need[i][j] = max[i][j] - allocation[i][j];
	        needbak[i][j] = need[i][j];
	     }
     }
     //可用资源向量
     for(int i=0;i<available.length;i++){
     System.out.print("请输入系统中第" + i + "种资源的剩余量：");
     available[i] = s.nextInt();
     availablebak[i] = available[i];
     }
     System.out.println("初始化结果=============");
     System.out.println("         MAX          ALLOCATION          NEED           AVAILABLE");     
     for(int i=0;i<m;i++){
     System.out.print("P" + i + ": ");
     for(int j=0;j<n;j++){
        if(max[i][j]>9){//如果是两位数，控制格式，在数字前少输出一个" "。
         System.out.print(max[i][j] + " ");
        }else{
         System.out.print(" " + max[i][j] + " ");
        } 
     }
     System.out.print("     ");
     for(int j=0;j<n;j++){
        if(allocation[i][j]>9){
         System.out.print(allocation[i][j] + " ");
        }else{
           System.out.print(" " + allocation[i][j] + " ");
        }
     }
     System.out.print("     ");
     for(int j=0;j<n;j++){
        if(need[i][j]>9){
         System.out.print(need[i][j] + " ");
        }else{
           System.out.print(" " + need[i][j] + " ");
        }
     }
     if(i==0){
        System.out.print("     ");
        for(int j=0;j<n;j++){
         if(available[j]>9){
          System.out.print(available[j] + " ");
         }else{
            System.out.print(" " + available[j] + " ");
         }
        }
     }
     System.out.println();
     }     
     System.out.println("=====完成初始化=====");
     System.out.println();
}

public void deadlockAvoidance(){
   int[] security = new int[m];
   boolean[] param = new boolean[m];
   int[] tar = new int[n];
   int count = 0;
   int num1 = m+1;//计数器,每循环一遍所有进程就自减1
   int num2 = m;//计数器，每遇到一个被满足的进程就自减1
  
   while(num1>0){//如果num1==0，则说明依次循环下来没有能够满足的进程，因此中止   
    for(int i=0;i<m;i++){
     if(param[i]==false){//只有没有被满足的进程才可以进入内层循环
    	 param[i] = true;
    	 for(int j=0;j<n;j++){      
    		 tar[j] = available[j] - need[i][j];
    		 if(tar[j]<0){
    			 param[i] = false; 
    		 }
    	 }
    	 
	     if(param[i]==true){
	          for(int k=0;k<n;k++){
	               available[k] = available[k] + allocation[i][k];
	          }
	          security[count] = i;//记录以满足的进程号
	          count++;
	          num2--;
	      }
      }       
    }
    num1--;
    while((num2==0)&&(num1>0)){//找到安全序列就立刻结束
	     System.out.print("【安全序列】为：");
	     for(int i=0;i<m;i++){
		      if(i==(m-1)){
		    	  System.out.print("P" + security[i]);
		      }else{
		    	  System.out.print("P" + security[i] + "-->");
		      }     
	     }
	     System.out.println();
	     System.out.println("=====【死锁避免】结束=====");
	     System.out.println();
	     return;
    }
    while((num1==0)&&(num2>0)){//全部找完，还有进程没满足，就是没有安全序列
     System.out.println("抱歉！没有【安全序列】！");
     System.out.println("=====【死锁避免】结束=====");
     System.out.println();
     return;
    }
   }      
}

public void deadlockDetection(){
   Scanner sc = new Scanner(System.in);
   int key;
     int[] security = new int[m];
     boolean[] param = new boolean[m];
   int[] temp = new int[n];
   int[] tar = new int[n];
   int count = 0;
   int num1 = m+1;//计数器,每循环一遍所有进程就自减1
   int num2 = m;//计数器，每遇到一个被满足的进程就自减1
  
   for(int i=0;i<m;i++){//回复死锁检测前的状态
    for(int j=0;j<n;j++){    
     max[i][j] = maxbak[i][j];
     allocation[i][j] = allocationbak[i][j];
     need[i][j] = needbak[i][j];
     available[j] = availablebak[j];
    }   
   }
   System.out.println();
   System.out.println("死锁检测============");
   System.out.println("如果您此时想申请系统资源请输入【进程号】和"+n+"种【资源量】，系统将帮您判断是否可行。");
   System.out.print("您输入的进程号为：");
   key = sc.nextInt();
   for(int i=0;i<n;i++){
    System.out.print("您要申请的第"+i+"种资源的数量为：");
    temp[i] = sc.nextInt();
   }
   for(int i=0;i<n;i++){
    allocation[key][i] = allocation[key][i] + temp[i];
    need[key][i] = need[key][i] - temp[i];
    if(need[key][i]<0){
     System.out.println("申请资源大于所需资源，系统【不能】分配资源！");
     for(int k=0;k<m;k++){//回复死锁检测前的状态
      for(int j=0;j<n;j++){
       if(k==0){
        available[j] = availablebak[j];
       }
       max[k][j] = maxbak[k][j];
       allocation[k][j] = allocationbak[k][j];
       need[k][j] = needbak[k][j];
      }   
     }
     return;
    }
    available[i] = available[i] - temp[i];
    if(available[i]<0){
     System.out.println("申请资源大于系统所剩资源，系统【不能】分配资源！");
     for(int k=0;k<m;k++){//回复死锁检测前的状态
      for(int j=0;j<n;j++){
       if(k==0){
        available[j] = availablebak[j];
       }
       max[k][j] = maxbak[k][j];
       allocation[k][j] = allocationbak[k][j];
       need[k][j] = needbak[k][j];
      }   
     }
     return;
    }  
   }
   System.out.println("申请资源时各进程的状态=============");
     System.out.println("         MAX          ALLOCATION          NEED           AVAILABLE");     
     for(int i=0;i<m;i++){
     System.out.print("P" + i + ": ");
     for(int j=0;j<n;j++){
        if(max[i][j]>9){
         System.out.print(max[i][j] + " ");
        }else{
         System.out.print(" " + max[i][j] + " ");
        } 
     }
     System.out.print("     ");
     for(int j=0;j<n;j++){
        if(allocation[i][j]>9){
         System.out.print(allocation[i][j] + " ");
        }else{
           System.out.print(" " + allocation[i][j] + " ");
        }
     }
     System.out.print("     ");
     for(int j=0;j<n;j++){
        if(need[i][j]>9){
         System.out.print(need[i][j] + " ");
        }else{
           System.out.print(" " + need[i][j] + " ");
        }
     }
     if(i==0){
        System.out.print("     ");
        for(int j=0;j<n;j++){
         if(available[j]>9){
          System.out.print(available[j] + " ");
         }else{
            System.out.print(" " + available[j] + " ");
         }
        }
     }
     System.out.println();
     }     
     System.out.println("=====完成状态展示=====");
     System.out.println();
   while(num1>0){//如果num1==0，则说明依次循环下来没有能够满足的进程，因此中止   
    for(int i=0;i<m;i++){
     if(param[i]==false){//只有没有被满足的进程才可以进入内层循环
      param[i] = true;
      for(int j=0;j<n;j++){      
       tar[j] = available[j] - need[i][j];
       if(tar[j]<0){
        param[i] = false; 
       }
      }
      if(param[i]==true){
                  for(int k=0;k<n;k++){
                  available[k] = available[k] + allocation[i][k];
                  }
                  security[count] = i;//记录以满足的进程号
                  count++;
                  num2--;
      }
     }       
    }
    num1--;
    while((num2==0)&&(num1>0)){
     System.out.print("【安全序列】为：");
     for(int i=0;i<m;i++){
      if(i==(m-1)){
       System.out.print("P" + security[i]);
      }else{
       System.out.print("P" + security[i] + "-->");
      }     
     }
     System.out.println();
     System.out.println("可以产生新的安全序列！系统【能】将申请的资源分配给P"+ key +"！");
     System.out.println("=====死锁检测结束=====");
     System.out.println();
     return;
    }
    while((num1==0)&&(num2>0)){
     System.out.println("抱歉！【没有】安全序列！");
     System.out.println("系统【不能】将申请的资源分配给P"+ key +"！");
     System.out.println("=====死锁检测结束=====");
     System.out.println();
     return;
    }
   } 
}
}


