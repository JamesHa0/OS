#include <windows.h>
#include <iostream>

using namespace std;

// 子线程函数
DWORD WINAPI ThreadFunction(LPVOID lpParam) {
    cout << "Thread is Running!" << endl;
    Sleep(5000); // 挂起 5 秒
    ExitThread(0); // 退出线程
}

int main() {
    HANDLE hThread; // 线程句柄
    DWORD threadId; // 线程 ID

    // 创建子线程
    hThread = CreateThread(NULL, 0, ThreadFunction, NULL, 0, &threadId);
    if (hThread == NULL) {
        cerr << "Failed to create thread!" << endl;
        return 1;
    }

    // 等待子线程结束
    WaitForSingleObject(hThread, INFINITE);

    // 关闭线程句柄
    CloseHandle(hThread);

    return 0;
}
