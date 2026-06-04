#!/usr/bin/env python3
"""
Playwright 测试脚本 - 测试本地前端访问
测试目标: http://localhost:5174
"""

import asyncio
from playwright.async_api import async_playwright, Page, Browser, ConsoleMessage


async def capture_console_errors(page: Page) -> list:
    """捕获浏览器控制台错误信息"""
    errors = []

    def handle_console(msg: ConsoleMessage):
        if msg.type in ["error", "warning"]:
            errors.append({
                "type": msg.type,
                "text": msg.text,
                "location": msg.location
            })

    page.on("console", handle_console)
    return errors


async def test_homepage(browser: Browser) -> dict:
    """测试首页"""
    print("\n" + "="*60)
    print("📋 测试首页: http://localhost:5174")
    print("="*60)

    result = {
        "url": "http://localhost:5174",
        "success": False,
        "title": "",
        "has_content": False,
        "content_length": 0,
        "screenshot": "/tmp/admin_test.png",
        "console_errors": [],
        "error_message": ""
    }

    try:
        context = await browser.new_context(viewport={"width": 1920, "height": 1080})
        page = await context.new_page()

        # 捕获控制台错误
        console_errors = await capture_console_errors(page)

        # 导航到首页
        print(f"🌐 正在导航到 {result['url']}...")
        response = await page.goto(result["url"], wait_until="networkidle", timeout=30000)

        if response is None:
            result["error_message"] = "无法获取页面响应"
            print(f"❌ 错误: {result['error_message']}")
            return result

        # 检查响应状态
        status = response.status
        print(f"✅ 响应状态码: {status}")

        if status >= 400:
            result["error_message"] = f"HTTP 错误: {status}"
            print(f"❌ 错误: {result['error_message']}")
            return result

        # 获取页面标题
        title = await page.title()
        result["title"] = title
        print(f"📝 页面标题: {title}")

        # 等待页面完全加载
        await page.wait_for_load_state("domcontentloaded")

        # 获取页面内容
        content = await page.content()
        result["content_length"] = len(content)
        result["has_content"] = len(content) > 100

        # 检查是否有可见内容
        body_text = await page.evaluate("document.body.innerText.trim()")
        has_visible_content = len(body_text) > 10
        print(f"📄 页面内容长度: {result['content_length']} 字符")
        print(f"📖 可见文本长度: {len(body_text)} 字符")

        # 截图
        print(f"📸 正在截图保存到 {result['screenshot']}")
        await page.screenshot(path=result["screenshot"], full_page=True)

        # 收集控制台错误
        result["console_errors"] = console_errors

        # 判断测试结果
        if status == 200 and has_visible_content and not console_errors:
            result["success"] = True
            print("✅ 首页测试通过!")
        elif console_errors:
            result["success"] = False
            print(f"⚠️  发现 {len(console_errors)} 个控制台错误/警告:")
            for i, err in enumerate(console_errors[:5], 1):
                print(f"   {i}. [{err['type'].upper()}] {err['text']}")
        else:
            print("⚠️  页面加载但可能存在问题")

        await context.close()

    except Exception as e:
        result["error_message"] = str(e)
        print(f"❌ 异常: {result['error_message']}")

    return result


async def test_member_page(browser: Browser) -> dict:
    """测试成员管理页面"""
    print("\n" + "="*60)
    print("📋 测试成员管理页面: http://localhost:5174/member/list")
    print("="*60)

    result = {
        "url": "http://localhost:5174/member/list",
        "success": False,
        "title": "",
        "has_content": False,
        "content_length": 0,
        "screenshot": "/tmp/admin_member.png",
        "console_errors": [],
        "error_message": ""
    }

    try:
        context = await browser.new_context(viewport={"width": 1920, "height": 1080})
        page = await context.new_page()

        # 捕获控制台错误
        console_errors = await capture_console_errors(page)

        # 导航到成员管理页面
        print(f"🌐 正在导航到 {result['url']}...")
        response = await page.goto(result["url"], wait_until="networkidle", timeout=30000)

        if response is None:
            result["error_message"] = "无法获取页面响应"
            print(f"❌ 错误: {result['error_message']}")
            return result

        # 检查响应状态
        status = response.status
        print(f"✅ 响应状态码: {status}")

        if status >= 400:
            result["error_message"] = f"HTTP 错误: {status}"
            print(f"❌ 错误: {result['error_message']}")
            return result

        # 获取页面标题
        title = await page.title()
        result["title"] = title
        print(f"📝 页面标题: {title}")

        # 等待页面完全加载
        await page.wait_for_load_state("domcontentloaded")

        # 获取页面内容
        content = await page.content()
        result["content_length"] = len(content)
        result["has_content"] = len(content) > 100

        # 检查是否有可见内容
        body_text = await page.evaluate("document.body.innerText.trim()")
        has_visible_content = len(body_text) > 10
        print(f"📄 页面内容长度: {result['content_length']} 字符")
        print(f"📖 可见文本长度: {len(body_text)} 字符")

        # 截图
        print(f"📸 正在截图保存到 {result['screenshot']}")
        await page.screenshot(path=result["screenshot"], full_page=True)

        # 收集控制台错误
        result["console_errors"] = console_errors

        # 判断测试结果
        if status == 200 and has_visible_content and not console_errors:
            result["success"] = True
            print("✅ 成员管理页面测试通过!")
        elif console_errors:
            result["success"] = False
            print(f"⚠️  发现 {len(console_errors)} 个控制台错误/警告:")
            for i, err in enumerate(console_errors[:5], 1):
                print(f"   {i}. [{err['type'].upper()}] {err['text']}")
        else:
            print("⚠️  页面加载但可能存在问题")

        await context.close()

    except Exception as e:
        result["error_message"] = str(e)
        print(f"❌ 异常: {result['error_message']}")

    return result


def generate_report(home_result: dict, member_result: dict):
    """生成测试报告"""
    print("\n" + "="*60)
    print("📊 测试报告总结")
    print("="*60)

    all_passed = home_result["success"] and member_result["success"]

    print(f"\n🏠 首页测试 (http://localhost:5174)")
    print(f"   状态: {'✅ 通过' if home_result['success'] else '❌ 失败'}")
    print(f"   标题: {home_result['title']}")
    print(f"   内容长度: {home_result['content_length']} 字符")
    print(f"   控制台错误: {len(home_result['console_errors'])} 个")
    if home_result["error_message"]:
        print(f"   错误信息: {home_result['error_message']}")
    print(f"   截图: {home_result['screenshot']}")

    print(f"\n👥 成员管理页面测试 (http://localhost:5174/member/list)")
    print(f"   状态: {'✅ 通过' if member_result['success'] else '❌ 失败'}")
    print(f"   标题: {member_result['title']}")
    print(f"   内容长度: {member_result['content_length']} 字符")
    print(f"   控制台错误: {len(member_result['console_errors'])} 个")
    if member_result["error_message"]:
        print(f"   错误信息: {member_result['error_message']}")
    print(f"   截图: {member_result['screenshot']}")

    print(f"\n{'='*60}")
    if all_passed:
        print("🎉 所有测试通过! 前端应用正常运行。")
    else:
        print("⚠️  部分测试未通过，请检查上述详细信息。")
    print("="*60)


async def main():
    """主函数"""
    print("🚀 启动 Playwright 浏览器测试...")
    print(f"⏰ 测试时间: {__import__('datetime').datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")

    async with async_playwright() as p:
        # 启动 headless chromium 浏览器
        print("\n🔧 正在启动 headless Chromium 浏览器...")
        browser = await p.chromium.launch(
            headless=True,
            args=[
                '--no-sandbox',
                '--disable-setuid-sandbox',
                '--disable-dev-shm-usage',
                '--disable-gpu'
            ]
        )
        print("✅ 浏览器启动成功!")

        try:
            # 测试首页
            home_result = await test_homepage(browser)

            # 测试成员管理页面
            member_result = await test_member_page(browser)

            # 生成报告
            generate_report(home_result, member_result)

        finally:
            # 关闭浏览器
            print("\n🔒 正在关闭浏览器...")
            await browser.close()
            print("✅ 浏览器已关闭")


if __name__ == "__main__":
    asyncio.run(main())
