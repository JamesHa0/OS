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
	//互斥变量
	HANDLE h_Mutex;
	h_Mutex = OpenMutex(MUTEX_ALL_ACCESS,FALSE,"mutex_for_readcount");

	DWORD wait_for_mutex;//等待互斥变量所有权
	DWORD m_delay;  //延迟时间
	DWORD m_persist;//读文件持续时间
	int m_serial; //线程序号
	//从参数中获得信息
	m_serial=((ThreadInfo*)(p))->serial;
	m_delay=(DWORD)(((ThreadInfo*)(p))->delay*INTE_PER_SEC);
	m_persist=(DWORD)(((ThreadInfo*)(p))->persist*INTE_PER_SEC);
	Sleep(m_delay);//延迟时间

	printf("Reader thread %d sents the reading require.\n",m_serial);
	//等待互斥信号，保证对readcount的访问、修改互斥
	wait_for_mutex=WaitForSingleObject(h_Mutex,-1);
	//读者数目增加
	readcount++;
	if(readcount ==1)
	{
		//第一个读者，使写者等待
		EnterCriticalSection(&RP_Write);
	}
	ReleaseMutex(h_Mutex);//释放互斥信号
	//读文件
	printf("Reader thread %d begins to read file.\n",m_serial);
	Sleep(m_persist);

	//退出线程
	printf("Reader thread %d finished reading file.\n",m_serial);
	//等待互斥信号，保证对readcount的访问、修改互斥
	wait_for_mutex = WaitForSingleObject(h_Mutex,-1);
	//读者数目减少
	readcount--;
	if(readcount==0)
	{
		//如果所有读者读完，唤醒写者
		LeaveCriticalSection(&RP_Write);
	}
	ReleaseMutex(h_Mutex);//释放互斥信号量
}


//////////////////////////////////////////////////////////////////////////
void RP_WriterThread(void* p)
{
	DWORD m_delay;//延迟时间
	DWORD m_persist;//写文件持续时间
	int m_serial;//线程序号
	//从参数中获得信息
	m_serial=((ThreadInfo*)(p))->serial;
    m_delay=(DWORD)(((ThreadInfo*)(p))->delay*INTE_PER_SEC);
	m_persist=(DWORD)(((ThreadInfo*)(p))->persist*INTE_PER_SEC);
	Sleep(m_delay);//延迟时间
	
	printf("Writer thread %d sents the writing require.\n",m_serial);
	//等待资源
	EnterCriticalSection(&RP_Write);

	//写文件
	printf("Writer thread %d begins to write to the file.\n",m_serial);
	Sleep(m_persist);

    	//退出线程
	printf("Writer thread %d finished writing to thefile.\n",m_serial);
    	//释放资源
    	LeaveCriticalSection(&RP_Write);

}

/////////////////////////////////////////////////////////////
//读者优先处理函数
void ReaderPriority(char *file)
{
	DWORD n_thread=0;  //线程数目
	DWORD thread_ID;
	DWORD wait_for_all; //等待所有线程结束

	//互斥对象
	HANDLE h_Mutex;
	h_Mutex=CreateMutex(NULL,FALSE,"mutex_for_readcount");

	//线程对象的数组
	HANDLE h_Thread[MAX_THREAD_NUM];
	ThreadInfo thread_info[MAX_THREAD_NUM];

	readcount=0;
	InitializeCriticalSection(&RP_Write);
	ifstream inFile;
	inFile.open(file);
	printf("Reader Priority:\n\n");
	while(inFile)
	{
		//读入每一个读者、写者的信息
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
			//创建读者线程
			h_Thread[i]=CreateThread(NULL,0,(LPTHREAD_START_ROUTINE)(RP_ReaderThread),&thread_info[i],0,&thread_ID);
		}
		else{
			//创建写者线程
			h_Thread[i]=CreateThread(NULL,0,(LPTHREAD_START_ROUTINE)(RP_WriterThread),&thread_info[i],0,&thread_ID);
		}
	}
	//等待所有线程结束
	wait_for_all=WaitForMultipleObjects(n_thread,h_Thread,TRUE,-1);
	printf("All reader and writer have finished operating.\n");
}

///////////////////////////////////////////////////
//写者优先-读者线程
void WP_ReaderThread(void *p)
{
	//互斥变量
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
	Sleep(m_delay);//延迟时间

	printf("Reader thread %d sents the reading require.\n",m_serial);
	//wait_for_mutex1=WaitForSingleObject(h_Mutex1,-1);
	
	//
	EnterCriticalSection(&cs_Read);
	//阻塞互斥对象mutex2,保证对readcount的访问、修改互斥
	wait_for_mutex2=WaitForSingleObject(h_Mutex2,-1);
	
	//LeaveCriticalSection(&cs_Read);
	
	//修改读者数目
		readcount++;
	if(readcount ==1)
	{
		//如果是第一个读者，等待写者写完
		EnterCriticalSection(&cs_Write);
	}
	ReleaseMutex(h_Mutex2);//释放互斥信号mutex2
	//让其他读者进入临界区
	LeaveCriticalSection(&cs_Read);
	//ReleaseMutex(h_Mutex1);
	//读文件
	printf("Reader thread %d begins to read file.\n",m_serial);
	Sleep(m_persist);

	//退出线程
	printf("Reader thread %d finished reading file.\n",m_serial);
	//阻塞mutex2，保证对readcount的访问、修改互斥
	wait_for_mutex2 = WaitForSingleObject(h_Mutex2,-1);
	//读者数目减少
	readcount--;
	if(readcount==0)
	{
		//如果所有读者读完，唤醒写者
		LeaveCriticalSection(&cs_Write);
	}
	ReleaseMutex(h_Mutex2);//释放互斥信号量
}


////////////////////////////////////////////////////////////////////
//写者优先-写者进程

void WP_WriterThread(void* p)
{
	DWORD m_delay;
	DWORD m_persist;
	int m_serial;
	DWORD wait_for_mutex3;

	//互斥对象
	HANDLE h_Mutex3;
	h_Mutex3=OpenMutex(MUTEX_ALL_ACCESS,FALSE,"mutex3");
	//从参数中获得信息
	m_serial=((ThreadInfo*)(p))->serial;
	m_delay=(DWORD)(((ThreadInfo*)(p))->delay*INTE_PER_SEC);
	m_persist=(DWORD)(((ThreadInfo*)(p))->persist*INTE_PER_SEC);
	Sleep(m_delay);

	printf("Writer thread %d sents the writing require.\n",m_serial);
	//阻塞互斥对象mutex3,保证对writecount的访问、修改互斥
	wait_for_mutex3=WaitForSingleObject(h_Mutex3,-1);
	writecount++; //修改写者数目
	if(writecount==1)
	{
		//第一个写者，等待读者读完
		EnterCriticalSection(&cs_Read);
	}
	ReleaseMutex(h_Mutex3);

	EnterCriticalSection(&cs_Write);

	printf("Writer thread %d begins to write to the file.\n",m_serial);
	Sleep(m_persist);

	printf("Writer thread %d finishing writing to the file.\n",m_serial);
	LeaveCriticalSection(&cs_Write);

	//阻塞互斥对象mutex3,保证对writecount的访问、修改互斥
	wait_for_mutex3=WaitForSingleObject(h_Mutex3,-1);
	writecount--;
	if(writecount ==0)
	{
		LeaveCriticalSection(&cs_Read);
	}
	ReleaseMutex(h_Mutex3);
}

//////////////////////////////
//写者优先处理函数
void WriterPriority(char* file)
{
	DWORD n_thread =0;
	DWORD thread_ID;
	DWORD wait_for_all;

	//互斥对象
	HANDLE h_Mutex1;
	h_Mutex1=CreateMutex(NULL,FALSE,"mutex1");
	HANDLE h_Mutex2;
	h_Mutex2=CreateMutex(NULL,FALSE,"mutex2");
	HANDLE h_Mutex3;
	h_Mutex3=CreateMutex(NULL,FALSE,"mutex3");

	//线程对象
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
		//读入每一个读者、写者的信息
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
			//创建读者线程
			h_Thread[i]=CreateThread(NULL,0,(LPTHREAD_START_ROUTINE)(WP_ReaderThread),&thread_info[i],0,&thread_ID);
		}
		else{
			//创建写者线程
			h_Thread[i]=CreateThread(NULL,0,(LPTHREAD_START_ROUTINE)(WP_WriterThread),&thread_info[i],0,&thread_ID);
		}
	}
	//等待所有线程结束
	wait_for_all=WaitForMultipleObjects(n_thread,h_Thread,TRUE,-1);
	printf("All reader and writer have finished operating.\n");
}



//主函数
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
