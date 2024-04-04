#include <windows.h>
#include <stdio.h>
#include <time.h>

void ThreadName1();
void ThreadName2();
static HANDLE hHandle1 = NULL;
DWORD dwThreadID1;

int main(int argc, TCHAR* argv[], TCHAR* envp[])
{
    int nRetCode = 0;
    HANDLE hHandle1 = CreateThread((LPSECURITY_ATTRIBUTES)NULL,
        0,
        (LPTHREAD_START_ROUTINE)ThreadName1,
        (LPVOID)NULL,
        0,
        &dwThreadID1);

    HANDLE hHandle2 = CreateThread((LPSECURITY_ATTRIBUTES)NULL,
        0,
        (LPTHREAD_START_ROUTINE)ThreadName2,
        (LPVOID)NULL,
        0,
        &dwThreadID1);
    CloseHandle(hHandle2);
    CloseHandle(hHandle1);
    ExitThread(0);
    return nRetCode;
}
void ThreadName1()
{
    printf("Thread is Running!\n");
    Sleep(5000);
    printf("5秒后退出!\n");
}
void ThreadName2()
{
    for (int i = 0; i < 5; i++) {
        time_t now = time(0);
        struct tm* timeinfo = localtime(&now);
        printf("当前时间: %02d:%02d:%02d\n", timeinfo->tm_hour, timeinfo->tm_min, timeinfo->tm_sec);
        Sleep(1000);
    }
}
