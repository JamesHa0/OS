#include "windows.h"
#include <conio.h>
#include <stdlib.h>
#include <fstream.h>
#include <io.h>
#include <string.h> 
#include <stdio.h>

#define READER 'R'
#define	WRITER 'W'
#define INTE_PER_SEC 1000
#define MAX_THREAD_NUM 64
#define	MAX_FILE_NUM 32
#define MAX_STR_LEN 32

int readcount = 0;
int writecount = 0;

CRITICAL_SECTION RP_Write;
CRITICAL_SECTION cs_Write;
CRITICAL_SECTION cs_Read;
struct ThreadInfo
{
	int serial;
	char entity;
	double delay;
	double persist;
};
void RP_ReaderThread(void *p)
{
	//�������
	HANDLE h_Mutex;
	h_Mutex = OpenMutex(MUTEX_ALL_ACCESS,FALSE,"mutex_for_readcount");

	DWORD wait_for_mutex;//�ȴ������������Ȩ
	DWORD m_delay;  //�ӳ�ʱ��
	DWORD m_persist;//���ļ�����ʱ��
	int m_serial; //�߳����
	//�Ӳ����л����Ϣ
	m_serial=((ThreadInfo*)(p))->serial;
	m_delay=(DWORD)(((ThreadInfo*)(p))->delay*INTE_PER_SEC);
	m_persist=(DWORD)(((ThreadInfo*)(p))->persist*INTE_PER_SEC);
	Sleep(m_delay);//�ӳ�ʱ��

	printf("Reader thread %d sents the reading require.\n",m_serial);
	//�ȴ������źţ���֤��readcount�ķ��ʡ��޸Ļ���
	wait_for_mutex=WaitForSingleObject(h_Mutex,-1);
	//������Ŀ����
	readcount++;
	if(readcount ==1)
	{
		//��һ�����ߣ�ʹд�ߵȴ�
		EnterCriticalSection(&RP_Write);
	}
	ReleaseMutex(h_Mutex);//�ͷŻ����ź�
	//���ļ�
	printf("Reader thread %d begins to read file.\n",m_serial);
	Sleep(m_persist);

	//�˳��߳�
	printf("Reader thread %d finished reading file.\n",m_serial);
	//�ȴ������źţ���֤��readcount�ķ��ʡ��޸Ļ���
	wait_for_mutex = WaitForSingleObject(h_Mutex,-1);
	//������Ŀ����
	readcount--;
	if(readcount==0)
	{
		//������ж��߶��꣬����д��
		LeaveCriticalSection(&RP_Write);
	}
	ReleaseMutex(h_Mutex);//�ͷŻ����ź���
}


//////////////////////////////////////////////////////////////////////////
void RP_WriterThread(void* p)
{
	DWORD m_delay;//�ӳ�ʱ��
	DWORD m_persist;//д�ļ�����ʱ��
	int m_serial;//�߳����
	//�Ӳ����л����Ϣ
	m_serial=((ThreadInfo*)(p))->serial;
    m_delay=(DWORD)(((ThreadInfo*)(p))->delay*INTE_PER_SEC);
	m_persist=(DWORD)(((ThreadInfo*)(p))->persist*INTE_PER_SEC);
	Sleep(m_delay);//�ӳ�ʱ��
	
	printf("Writer thread %d sents the writing require.\n",m_serial);
	//�ȴ���Դ
	EnterCriticalSection(&RP_Write);

	//д�ļ�
	printf("Writer thread %d begins to write to the file.\n",m_serial);
	Sleep(m_persist);

    	//�˳��߳�
	printf("Writer thread %d finished writing to thefile.\n",m_serial);
    	//�ͷ���Դ
    	LeaveCriticalSection(&RP_Write);

}

/////////////////////////////////////////////////////////////
//�������ȴ�����
void ReaderPriority(char *file)
{
	DWORD n_thread=0;  //�߳���Ŀ
	DWORD thread_ID;
	DWORD wait_for_all; //�ȴ������߳̽���

	//�������
	HANDLE h_Mutex;
	h_Mutex=CreateMutex(NULL,FALSE,"mutex_for_readcount");

	//�̶߳��������
	HANDLE h_Thread[MAX_THREAD_NUM];
	ThreadInfo thread_info[MAX_THREAD_NUM];

	readcount=0;
	InitializeCriticalSection(&RP_Write);
	ifstream inFile;
	inFile.open(file);
	printf("Reader Priority:\n\n");
	while(inFile)
	{
		//����ÿһ�����ߡ�д�ߵ���Ϣ
		inFile>>thread_info[n_thread].serial;
		inFile>>thread_info[n_thread].entity;
		inFile>>thread_info[n_thread].delay;
		inFile>>thread_info[n_thread++].persist;
		inFile.get();
	}
	for(int i=0;i<(int)(n_thread);i++)
	{
		if(thread_info[i].entity==READER||thread_info[i].entity=='r')
		{
			//���������߳�
			h_Thread[i]=CreateThread(NULL,0,(LPTHREAD_START_ROUTINE)(RP_ReaderThread),&thread_info[i],0,&thread_ID);
		}
		else{
			//����д���߳�
			h_Thread[i]=CreateThread(NULL,0,(LPTHREAD_START_ROUTINE)(RP_WriterThread),&thread_info[i],0,&thread_ID);
		}
	}
	//�ȴ������߳̽���
	wait_for_all=WaitForMultipleObjects(n_thread,h_Thread,TRUE,-1);
	printf("All reader and writer have finished operating.\n");
}

///////////////////////////////////////////////////
//д������-�����߳�
void WP_ReaderThread(void *p)
{
	//�������
	HANDLE h_Mutex1;
	h_Mutex1=OpenMutex(MUTEX_ALL_ACCESS,FALSE,"mutex1");
	HANDLE h_Mutex2;
	h_Mutex2=OpenMutex(MUTEX_ALL_ACCESS,FALSE,"mutex2");

	DWORD wait_for_mutex1;
	DWORD wait_for_mutex2;
	DWORD m_delay;
	DWORD m_persist;
	int m_serial;

    m_serial=((ThreadInfo*)(p))->serial;
	m_delay=(DWORD)(((ThreadInfo*)(p))->delay*INTE_PER_SEC);
	m_persist=(DWORD)(((ThreadInfo*)(p))->persist*INTE_PER_SEC);
	Sleep(m_delay);//�ӳ�ʱ��

	printf("Reader thread %d sents the reading require.\n",m_serial);
	//wait_for_mutex1=WaitForSingleObject(h_Mutex1,-1);
	
	//
	EnterCriticalSection(&cs_Read);
	//�����������mutex2,��֤��readcount�ķ��ʡ��޸Ļ���
	wait_for_mutex2=WaitForSingleObject(h_Mutex2,-1);
	
	//LeaveCriticalSection(&cs_Read);
	
	//�޸Ķ�����Ŀ
		readcount++;
	if(readcount ==1)
	{
		//����ǵ�һ�����ߣ��ȴ�д��д��
		EnterCriticalSection(&cs_Write);
	}
	ReleaseMutex(h_Mutex2);//�ͷŻ����ź�mutex2
	//���������߽����ٽ���
	LeaveCriticalSection(&cs_Read);
	//ReleaseMutex(h_Mutex1);
	//���ļ�
	printf("Reader thread %d begins to read file.\n",m_serial);
	Sleep(m_persist);

	//�˳��߳�
	printf("Reader thread %d finished reading file.\n",m_serial);
	//����mutex2����֤��readcount�ķ��ʡ��޸Ļ���
	wait_for_mutex2 = WaitForSingleObject(h_Mutex2,-1);
	//������Ŀ����
	readcount--;
	if(readcount==0)
	{
		//������ж��߶��꣬����д��
		LeaveCriticalSection(&cs_Write);
	}
	ReleaseMutex(h_Mutex2);//�ͷŻ����ź���
}


////////////////////////////////////////////////////////////////////
//д������-д�߽���

void WP_WriterThread(void* p)
{
	DWORD m_delay;
	DWORD m_persist;
	int m_serial;
	DWORD wait_for_mutex3;

	//�������
	HANDLE h_Mutex3;
	h_Mutex3=OpenMutex(MUTEX_ALL_ACCESS,FALSE,"mutex3");
	//�Ӳ����л����Ϣ
	m_serial=((ThreadInfo*)(p))->serial;
	m_delay=(DWORD)(((ThreadInfo*)(p))->delay*INTE_PER_SEC);
	m_persist=(DWORD)(((ThreadInfo*)(p))->persist*INTE_PER_SEC);
	Sleep(m_delay);

	printf("Writer thread %d sents the writing require.\n",m_serial);
	//�����������mutex3,��֤��writecount�ķ��ʡ��޸Ļ���
	wait_for_mutex3=WaitForSingleObject(h_Mutex3,-1);
	writecount++; //�޸�д����Ŀ
	if(writecount==1)
	{
		//��һ��д�ߣ��ȴ����߶���
		EnterCriticalSection(&cs_Read);
	}
	ReleaseMutex(h_Mutex3);

	EnterCriticalSection(&cs_Write);

	printf("Writer thread %d begins to write to the file.\n",m_serial);
	Sleep(m_persist);

	printf("Writer thread %d finishing writing to the file.\n",m_serial);
	LeaveCriticalSection(&cs_Write);

	//�����������mutex3,��֤��writecount�ķ��ʡ��޸Ļ���
	wait_for_mutex3=WaitForSingleObject(h_Mutex3,-1);
	writecount--;
	if(writecount ==0)
	{
		LeaveCriticalSection(&cs_Read);
	}
	ReleaseMutex(h_Mutex3);
}

//////////////////////////////
//д�����ȴ�����
void WriterPriority(char* file)
{
	DWORD n_thread =0;
	DWORD thread_ID;
	DWORD wait_for_all;

	//�������
	HANDLE h_Mutex1;
	h_Mutex1=CreateMutex(NULL,FALSE,"mutex1");
	HANDLE h_Mutex2;
	h_Mutex2=CreateMutex(NULL,FALSE,"mutex2");
	HANDLE h_Mutex3;
	h_Mutex3=CreateMutex(NULL,FALSE,"mutex3");

	//�̶߳���
	HANDLE h_Thread[MAX_THREAD_NUM];
	ThreadInfo thread_info[MAX_THREAD_NUM];

	readcount=0;
	writecount=0;
	InitializeCriticalSection(&cs_Write);
	InitializeCriticalSection(&cs_Read);
	ifstream inFile;
	inFile.open(file);
	printf("Writer Priority:\n\n");
	while(inFile)
	{
		//����ÿһ�����ߡ�д�ߵ���Ϣ
		inFile>>thread_info[n_thread].serial;
		inFile>>thread_info[n_thread].entity;
		inFile>>thread_info[n_thread].delay;
		inFile>>thread_info[n_thread++].persist;
		inFile.get();
	}
	for(int i=0;i<(int)(n_thread);i++)
	{
		if(thread_info[i].entity==READER||thread_info[i].entity=='r')
		{
			//���������߳�
			h_Thread[i]=CreateThread(NULL,0,(LPTHREAD_START_ROUTINE)(WP_ReaderThread),&thread_info[i],0,&thread_ID);
		}
		else{
			//����д���߳�
			h_Thread[i]=CreateThread(NULL,0,(LPTHREAD_START_ROUTINE)(WP_WriterThread),&thread_info[i],0,&thread_ID);
		}
	}
	//�ȴ������߳̽���
	wait_for_all=WaitForMultipleObjects(n_thread,h_Thread,TRUE,-1);
	printf("All reader and writer have finished operating.\n");
}



//������
int main(int argc,char*argv[])
{
	char ch;
	while(true)
	{
		printf("**************************************\n");
		printf("          1:Reader Priority\n");
		printf("          2:Writer Priority\n");
		printf("          3:Exit to Windows\n");
        printf("**************************************\n");
		printf("Enter your choice(1,2 or 3):   ");
	
		do{
			ch=(char)_getch();
		}while(ch!='1'&&ch!='2'&&ch!='3');
	
		system("cls");
	
		if(ch=='3')
			return 0;
		else if(ch=='1')
			ReaderPriority("thread.dat");
		else
			WriterPriority("thread.dat");
	
		printf("\nPress Any Key to Continue");
		_getch();
		system("cls");

    }
	return 0;
}
