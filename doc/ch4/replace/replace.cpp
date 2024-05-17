#define INVALID -1


#define total_instruction 320 //ָ������
#define total_vp 32           //����ҳ����
//#define clear_period 50     //��������

typedef struct  //ҳ��ṹ
{
	int pn,pfn,counter,time;   //ҳ�š�ҳ����Ч��Ч��һ�������ڷ��ʸ�ҳ�����������ʱ��
}pl_type;


pl_type pl[total_vp];          //ҳ��ṹ����



struct pfc_struct //ҳ����ƽṹ
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
	srand(_getpid()*10);//ÿ������ʱ���̺Ų�ͬ��������Ϊ��ʼ����������е�����
	S=319*rand()/32767 ;   //�õ�һ��0��319֮���һ����

    for (i=0;i<total_instruction;i+=4)//����ָ�����
	{
		a[i]=S;//��ѡһָ����ʵ�
		a[i+1]=a[i]+1;//˳��ִ��һ��ָ��
		if (a[i+1]==320)
			a[i+1]=0;
		a[i+2]=a[i]*rand()/32767;//ִ��ǰ��ַָ��
		a[i+3]=319-a[i+2];//ִ�к��ַָ��
        //S=rand()*(318-a[i+2])/32767+a[i+2]+1;
		S=319*rand()/32767 ;
	}
	for(i=0;i<total_instruction;i++)//��ָ�����б任��ҳ��ַ����ҳ��+ƫ����
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

void FIFO(int total_pf)  //total_pf���û�����ҳ����Ŀ
{
	int i;
	pfc_type *p;
	initialize(total_pf);
	busypf_head=busypf_tail=NULL;
    for(i=0;i<total_instruction;i++)
	{
		if(pl[page[i]].pfn==INVALID) {  //ҳ��ʧЧ
			diseffect++;             //ʧЧ����
		if(freepf_head==NULL)   //�޿���ҳ��ʱ�û�һҳ
		{
			p=busypf_head->next;
			pl[busypf_head->pn].pfn=INVALID;
			freepf_head=busypf_head;
			freepf_head->next=NULL;
			busypf_head=p;
		}
		p=freepf_head->next;        //�п���ҳ��ʱ����һҳ
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

void LRU(int total_pf)  //total_pf���û�����ҳ����Ŀ,�����������ڴ�pageframe��Ŀ
{
	int min,minj,i,j,present_time;

	initialize(total_pf);
	present_time=0;
	for(i=0;i<total_instruction;i++)
	{
		if(pl[page[i]].pfn==INVALID) {  //ҳ��ʧЧ
			diseffect++;             //ʧЧ����
		if(freepf_head==NULL)   //�޿���ҳ��ʱ�û�һҳ
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


		//�п���ҳ��ʱ����һҳ

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
	for(i=0;i<total_vp;i++)            //ҳ����ƽṹ���
	{
		pl[i].pn=i;pl[i].pfn =INVALID;
		pl[i].counter =0;pl[i].time =-1;
	}
	for(i=1;i<total_pf;i++)            //��������
	{
		pfc[i-1].next=&pfc[i];pfc[i-1].pfn =i-1;
	}

	pfc[total_pf-1].next =NULL;pfc[total_pf-1].pfn =total_pf-1;
	freepf_head=&pfc[0];
}
