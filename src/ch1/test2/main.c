#include <windows.h>
#include <stdio.h>

static HANDLE h1;// 线程句柄
static HANDLE hHandle1 = NULL;// 信号量句柄

void func();

int main(int argc,TCHAR* argv[],TCHAR* envp[])
{
    int nRetCode = 0;
    DWORD dwThreadID1;
    DWORD dRes,err;

    hHandle1 = CreateSemaphore(NULL,0,1,"SemaphoreName1");
    if(hHandle1 == NULL)
        printf("Semaphore Create Fail!\n");
    else printf("Semaphore Create Success!\n");

    hHandle1 = OpenSemaphore(SYNCHRONIZE|SEMAPHORE_MODIFY_STATE,
                             NULL,
                             "SemaphoreName1");
    if(hHandle1 == NULL)
        printf("Semaphore Open Fail!\n");
    else printf("Semaphore Open Success!\n");

    h1 = CreateThread((LPSECURITY_ATTRIBUTES)NULL,
                      0,
                      (LPTHREAD_START_ROUTINE)func,
                      (LPVOID)NULL,
                      0,&dwThreadID1);
    if (h1 == NULL)
        printf("Thread1 create Fail!\n");
    else printf("Thread1 create Success!\n");

    dRes = WaitForSingleObject(hHandle1,INFINITE);
    err = GetLastError();
    printf("WaitForSingleObject err = %d\n",err);

    if (dRes == WAIT_TIMEOUT)
        printf("TIMEOUT!dRes = %d\n",dRes);
    else if (dRes == WAIT_OBJECT_0)
        printf("WAIT_OBJECT!dRes = %d\n",dRes);
    else if (dRes == WAIT_ABANDONED)
        printf("WAIT_ABANDONED!dRes = %d\n",dRes);
    else printf("dRes = %d\n",dRes);

    CloseHandle(h1);
    CloseHandle(hHandle1);
    ExitThread(0);

    return nRetCode;

}

void func()
{
    BOOL rc;
    DWORD err;
    printf("Now In Thread!\n");
    rc = ReleaseSemaphore(hHandle1,1,NULL);
    err = GetLastError();
    printf("ReleaseSemaphore err = %d\n",err);
    if (rc == 0)
        printf("Semaphore Release Fail!\n");
    else printf("Semaphore Release Success!rc = %d\n",rc);
}
