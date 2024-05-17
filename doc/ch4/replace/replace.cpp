#define INVALID -1


#define total_instruction 320 //指令流长
#define total_vp 32           //虚拟页表长度
//#define clear_period 50     //清零周期

typedef struct  //页面结构
{
	int pn,pfn,counter,time;   //页号、页面有效无效、一个周期内访问该页面次数、访问时间
}pl_type;


pl_type pl[total_vp];          //页面结构数组



struct pfc_struct //页面控制结构
{
	int pn,pfn;
	struct pfc_struct *next;
};
typedef struct pfc_struct pfc_type;
pfc_type pfc[total_vp],*freepf_head,*busypf_head,*busypf_tail;

int diseffect;
int a[total_instruction];
int page[total_instruction],offset[total_instruction];


void initialize(int);
void FIFO(int);
void LRU(int);
//void OPT(int);


#include <stdio.h>
#include <stdlib.h>
#include <process.h>
int main()
{
	int S,i;
	srand(_getpid()*10);//每次运行时进程号不同，可以作为初始化随机数队列的种子
	S=319*rand()/32767 ;   //得到一个0到319之间的一个数

    for (i=0;i<total_instruction;i+=4)//产生指令队列
	{
		a[i]=S;//任选一指令访问点
		a[i+1]=a[i]+1;//顺序执行一条指令
		if (a[i+1]==320)
			a[i+1]=0;
		a[i+2]=a[i]*rand()/32767;//执行前地址指令
		a[i+3]=319-a[i+2];//执行后地址指令
        //S=rand()*(318-a[i+2])/32767+a[i+2]+1;
		S=319*rand()/32767 ;
	}
	for(i=0;i<total_instruction;i++)//将指令序列变换成页地址流：页号+偏移量
	{
		page[i]=a[i]/10;
		offset[i]=a[i]%10;
	}
	for(i=4;i<=32;i++)
	{
		printf("%2d page frames",i);
		FIFO(i);
		LRU(i);
		//OPT(i);
		//LFU(i);
		//NUR(i);
		printf("\n");
	}
    return 0;
}

void FIFO(int total_pf)  //total_pf是用户进程页面数目
{
	int i;
	pfc_type *p;
	initialize(total_pf);
	busypf_head=busypf_tail=NULL;
    for(i=0;i<total_instruction;i++)
	{
		if(pl[page[i]].pfn==INVALID) {  //页面失效
			diseffect++;             //失效计数
		if(freepf_head==NULL)   //无空闲页面时置换一页
		{
			p=busypf_head->next;
			pl[busypf_head->pn].pfn=INVALID;
			freepf_head=busypf_head;
			freepf_head->next=NULL;
			busypf_head=p;
		}
		p=freepf_head->next;        //有空闲页面时分配一页
		freepf_head->next=NULL;
		freepf_head->pn=page[i];
		pl[page[i]].pfn=freepf_head->pfn;
		if(busypf_tail==NULL)
			busypf_head=busypf_tail=freepf_head;
		else
		{
			busypf_tail->next=freepf_head;
			busypf_tail=freepf_head;
		}
		freepf_head=p;
	}

	}
	printf("FIFO():%6.4f      ",1-(float)diseffect/320);
}

void LRU(int total_pf)  //total_pf是用户进程页面数目,即进程物理内存pageframe数目
{
	int min,minj,i,j,present_time;

	initialize(total_pf);
	present_time=0;
	for(i=0;i<total_instruction;i++)
	{
		if(pl[page[i]].pfn==INVALID) {  //页面失效
			diseffect++;             //失效计数
		if(freepf_head==NULL)   //无空闲页面时置换一页
		{
			min=32767;
			for(j=0;j<total_vp;j++)
				if(min>pl[j].time && (pl[j].pfn !=INVALID))
					{min=pl[j].time ;minj=j;}
			freepf_head=&pfc[pl[minj].pfn];
			pl[minj].pfn=INVALID;
			pl[minj].time=-1;
			freepf_head->next =NULL;
		}


		//有空闲页面时分配一页

		pl[page[i]].pfn=freepf_head->pfn;
	    pl[page[i]].time=present_time;
		freepf_head=freepf_head->next;

		}

		else
          pl[page[i]].time=present_time;
		present_time++;

	}

	printf("LRU():%6.4f ",1-(float)diseffect/320);
}


void initialize(int total_pf)
{
	int i;
	diseffect=0;
	for(i=0;i<total_vp;i++)            //页面控制结构清空
	{
		pl[i].pn=i;pl[i].pfn =INVALID;
		pl[i].counter =0;pl[i].time =-1;
	}
	for(i=1;i<total_pf;i++)            //建立链接
	{
		pfc[i-1].next=&pfc[i];pfc[i-1].pfn =i-1;
	}

	pfc[total_pf-1].next =NULL;pfc[total_pf-1].pfn =total_pf-1;
	freepf_head=&pfc[0];
}
