import java.util.*;

public class  Banker{
public static void main(String[] args) {
   Scanner scanner = new Scanner(System.in);
   TheBanker tb = new TheBanker();
   tb.deadlockAvoidance();//��������
   int gate = 1;
   while(gate!=0){ 
     tb.deadlockDetection();//�������
     System.out.println("�����Ҫ����������Դ������\"1\"���˳�������\"0\"");
     System.out.print("�������ֵΪ��");
     gate = scanner.nextInt();
     System.out.println();
   }
   System.out.println("ʹ����죡�ڴ����´�ʹ�ã�");
}
}

class TheBanker{
int m;
int n;
int[][] max;
int[][] maxbak;//������
int[][] allocation;
int[][] allocationbak;//������
int[][] need;
int[][] needbak;//������
int[] available;
int[] availablebak;//������

public TheBanker(){  
   Scanner s = new Scanner(System.in);
   System.out.println("��ʼ��=============="); 
   System.out.print("����������ϵͳ�еġ����������͡���Դ����������");
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
     
     for(int i=0;i<max.length;i++){//��ʼ������MAX��ALLOCATION��NEED��AVAILABLE
    	 System.out.print("�����������" + i + "���̵ĸ���Դ�����������");
	     for(int j=0;j<max[i].length;j++){
	        max[i][j] = s.nextInt();
	        maxbak[i][j] = max[i][j];
	     }
     }     
     for(int i=0;i<allocation.length;i++){
    	 System.out.print("�����������" + i + "�������ѷ�����Դ��������");
	     for(int j=0;j<allocation[i].length;j++){
	        allocation[i][j] = s.nextInt();
	        allocationbak[i][j] = allocation[i][j];
	     }
     }
     //��������Զ������
     for(int i=0;i<need.length;i++){
	     for(int j=0;j<need[i].length;j++){
	        need[i][j] = max[i][j] - allocation[i][j];
	        needbak[i][j] = need[i][j];
	     }
     }
     //������Դ����
     for(int i=0;i<available.length;i++){
     System.out.print("������ϵͳ�е�" + i + "����Դ��ʣ������");
     available[i] = s.nextInt();
     availablebak[i] = available[i];
     }
     System.out.println("��ʼ�����=============");
     System.out.println("         MAX          ALLOCATION          NEED           AVAILABLE");     
     for(int i=0;i<m;i++){
     System.out.print("P" + i + ": ");
     for(int j=0;j<n;j++){
        if(max[i][j]>9){//�������λ�������Ƹ�ʽ��������ǰ�����һ��" "��
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
     System.out.println("=====��ɳ�ʼ��=====");
     System.out.println();
}

public void deadlockAvoidance(){
   int[] security = new int[m];
   boolean[] param = new boolean[m];
   int[] tar = new int[n];
   int count = 0;
   int num1 = m+1;//������,ÿѭ��һ�����н��̾��Լ�1
   int num2 = m;//��������ÿ����һ��������Ľ��̾��Լ�1
  
   while(num1>0){//���num1==0����˵������ѭ������û���ܹ�����Ľ��̣������ֹ   
    for(int i=0;i<m;i++){
     if(param[i]==false){//ֻ��û�б�����Ľ��̲ſ��Խ����ڲ�ѭ��
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
	          security[count] = i;//��¼������Ľ��̺�
	          count++;
	          num2--;
	      }
      }       
    }
    num1--;
    while((num2==0)&&(num1>0)){//�ҵ���ȫ���о����̽���
	     System.out.print("����ȫ���С�Ϊ��");
	     for(int i=0;i<m;i++){
		      if(i==(m-1)){
		    	  System.out.print("P" + security[i]);
		      }else{
		    	  System.out.print("P" + security[i] + "-->");
		      }     
	     }
	     System.out.println();
	     System.out.println("=====���������⡿����=====");
	     System.out.println();
	     return;
    }
    while((num1==0)&&(num2>0)){//ȫ�����꣬���н���û���㣬����û�а�ȫ����
     System.out.println("��Ǹ��û�С���ȫ���С���");
     System.out.println("=====���������⡿����=====");
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
   int num1 = m+1;//������,ÿѭ��һ�����н��̾��Լ�1
   int num2 = m;//��������ÿ����һ��������Ľ��̾��Լ�1
  
   for(int i=0;i<m;i++){//�ظ��������ǰ��״̬
    for(int j=0;j<n;j++){    
     max[i][j] = maxbak[i][j];
     allocation[i][j] = allocationbak[i][j];
     need[i][j] = needbak[i][j];
     available[j] = availablebak[j];
    }   
   }
   System.out.println();
   System.out.println("�������============");
   System.out.println("�������ʱ������ϵͳ��Դ�����롾���̺š���"+n+"�֡���Դ������ϵͳ�������ж��Ƿ���С�");
   System.out.print("������Ľ��̺�Ϊ��");
   key = sc.nextInt();
   for(int i=0;i<n;i++){
    System.out.print("��Ҫ����ĵ�"+i+"����Դ������Ϊ��");
    temp[i] = sc.nextInt();
   }
   for(int i=0;i<n;i++){
    allocation[key][i] = allocation[key][i] + temp[i];
    need[key][i] = need[key][i] - temp[i];
    if(need[key][i]<0){
     System.out.println("������Դ����������Դ��ϵͳ�����ܡ�������Դ��");
     for(int k=0;k<m;k++){//�ظ��������ǰ��״̬
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
     System.out.println("������Դ����ϵͳ��ʣ��Դ��ϵͳ�����ܡ�������Դ��");
     for(int k=0;k<m;k++){//�ظ��������ǰ��״̬
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
   System.out.println("������Դʱ�����̵�״̬=============");
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
     System.out.println("=====���״̬չʾ=====");
     System.out.println();
   while(num1>0){//���num1==0����˵������ѭ������û���ܹ�����Ľ��̣������ֹ   
    for(int i=0;i<m;i++){
     if(param[i]==false){//ֻ��û�б�����Ľ��̲ſ��Խ����ڲ�ѭ��
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
                  security[count] = i;//��¼������Ľ��̺�
                  count++;
                  num2--;
      }
     }       
    }
    num1--;
    while((num2==0)&&(num1>0)){
     System.out.print("����ȫ���С�Ϊ��");
     for(int i=0;i<m;i++){
      if(i==(m-1)){
       System.out.print("P" + security[i]);
      }else{
       System.out.print("P" + security[i] + "-->");
      }     
     }
     System.out.println();
     System.out.println("���Բ����µİ�ȫ���У�ϵͳ���ܡ����������Դ�����P"+ key +"��");
     System.out.println("=====����������=====");
     System.out.println();
     return;
    }
    while((num1==0)&&(num2>0)){
     System.out.println("��Ǹ����û�С���ȫ���У�");
     System.out.println("ϵͳ�����ܡ����������Դ�����P"+ key +"��");
     System.out.println("=====����������=====");
     System.out.println();
     return;
    }
   } 
}
}


