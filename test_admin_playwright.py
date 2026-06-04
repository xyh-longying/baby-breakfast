#!/usr/bin/env python3
"""
Playwright 自动化测试脚本 - 管理员登录和成员管理页面测试
"""

import asyncio
from playwright.async_api import async_playwright, TimeoutError as PlaywrightTimeout


async def test_admin_login_and_member_page():
    """执行管理员登录测试和成员管理页面检查"""

    results = {
        "login_success": False,
        "login_screenshot": None,
        "member_page_success": False,
        "member_screenshot": None,
        "checks": {},
        "errors": []
    }

    print("=" * 60)
    print("开始 Playwright 自动化测试")
    print("=" * 60)

    async with async_playwright() as p:
        # 1. 启动 headless chromium 浏览器
        print("\n[1/13] 启动 headless Chromium 浏览器...")
        browser = await p.chromium.launch(headless=True)
        context = await browser.new_context(viewport={"width": 1920, "height": 1080})
        page = await context.new_page()
        print("✓ 浏览器启动成功")

        try:
            # 2. 导航到登录页面
            print("\n[2/13] 导航到 http://localhost:5174 ...")
            await page.goto("http://localhost:5174", wait_until="networkidle", timeout=30000)
            current_url = page.url
            print(f"✓ 页面加载完成，当前 URL: {current_url}")
            results["checks"]["initial_url"] = current_url

            # 3. 等待页面加载完成（已在 goto 中处理）
            print("\n[3/13] 页面已加载完成 (networkidle)")

            # 4. 在用户名输入框输入 "admin"
            print("\n[4/13] 查找用户名输入框并输入 'admin'...")
            # 尝试多种选择器定位用户名输入框
            username_input = await page.wait_for_selector(
                'input[name="username"], input[type="text"], input[placeholder*="用户"], input[placeholder*="账号"]',
                timeout=10000
            )
            await username_input.fill("admin")
            print(f"✓ 用户名已输入: admin")

            # 5. 在密码输入框输入 "admin123"
            print("\n[5/13] 查找密码输入框并输入 'admin123'...")
            password_input = await page.wait_for_selector(
                'input[name="password"], input[type="password"], input[placeholder*="密码"]',
                timeout=10000
            )
            await password_input.fill("admin123")
            print("✓ 密码已输入: admin123")

            # 6. 点击"登录"按钮（橙色按钮）
            print("\n[6/13] 点击登录按钮...")
            # 尝试多种方式定位登录按钮
            login_button = await page.wait_for_selector(
                'button:has-text("登 录"), button:has-text("登录"), button[type="submit"], .login-btn, [class*="login"] button',
                timeout=10000
            )
            await login_button.click()
            print("✓ 已点击登录按钮")

            # 7. 等待页面跳转完成
            print("\n[7/13] 等待页面跳转...")
            # 等待导航到新页面或 dashboard
            try:
                await page.wait_for_load_state("networkidle", timeout=15000)
                await asyncio.sleep(1)  # 额外等待确保渲染完成
                after_login_url = page.url
                print(f"✓ 页面跳转完成，当前 URL: {after_login_url}")
                results["login_success"] = True
                results["checks"]["after_login_url"] = after_login_url
            except PlaywrightTimeoutError:
                print("⚠ 页面跳转超时，继续尝试后续操作")
                results["errors"].append("页面跳转超时")

            # 8. 截图保存到 /tmp/admin_after_login.png
            print("\n[8/13] 保存登录后截图...")
            screenshot_path = "/tmp/admin_after_login.png"
            await page.screenshot(path=screenshot_path, full_page=True)
            results["login_screenshot"] = screenshot_path
            print(f"✓ 截图已保存: {screenshot_path}")

            # 9. 导航到成员管理页面
            print("\n[9/13] 导航到 http://localhost:5174/member/list ...")
            await page.goto("http://localhost:5174/member/list", wait_until="networkidle", timeout=30000)
            member_url = page.url
            print(f"✓ 成员管理页面加载完成，当前 URL: {member_url}")

            # 10. 等待加载完成（已在 goto 中处理）
            print("\n[10/13] 页面已加载完成 (networkidle)")

            # 11. 截图保存到 /tmp/admin_member_list.png
            print("\n[11/13] 保存成员列表页面截图...")
            member_screenshot_path = "/tmp/admin_member_list.png"
            await page.screenshot(path=member_screenshot_path, full_page=True)
            results["member_screenshot"] = member_screenshot_path
            results["member_page_success"] = True
            print(f"✓ 截图已保存: {member_screenshot_path}")

            # 12. 检查成员管理页面元素
            print("\n[12/13] 检查成员管理页面元素...")

            # 检查表格是否存在
            table_check = await page.query_selector('table, .table, [class*="table"], .el-table, .ant-table')
            if table_check:
                results["checks"]["table_exists"] = True
                print("✓ 找到表格元素")
            else:
                results["checks"]["table_exists"] = False
                print("⚠ 未找到表格元素")

            # 检查新增成员按钮
            add_button = await page.query_selector(
                'button:has-text("新增"), button:has-text("添加"), button:has-text("添加成员"), '
                'button:has-text("新增成员"), [class*="add"] button, [class*="create"] button'
            )
            if add_button:
                results["checks"]["add_member_button_exists"] = True
                print("✓ 找到新增成员按钮")
            else:
                results["checks"]["add_member_button_exists"] = False
                print("⚠ 未找到新增成员按钮")

            # 检查页面标题或内容
            page_content = await page.content()
            has_member_text = "成员" in page_content or "member" in page_content.lower()
            results["checks"]["has_member_content"] = has_member_text
            if has_member_text:
                print("✓ 页面包含成员相关内容")
            else:
                print("⚠ 页面未检测到明确的成员相关内容")

        except Exception as e:
            error_msg = f"测试过程中发生错误: {str(e)}"
            print(f"\n✗ 错误: {error_msg}")
            results["errors"].append(error_msg)

            # 尝试保存当前状态的截图
            try:
                error_screenshot = "/tmp/error_screenshot.png"
                await page.screenshot(path=error_screenshot, full_page=True)
                print(f"✓ 错误状态截图已保存: {error_screenshot}")
            except:
                pass

        finally:
            # 关闭浏览器
            print("\n[13/13] 关闭浏览器...")
            await browser.close()
            print("✓ 浏览器已关闭")

    return results


async def main():
    """主函数：执行测试并报告结果"""
    results = await test_admin_login_and_member_page()

    # 13. 报告所有结果和截图路径
    print("\n" + "=" * 60)
    print("测试报告")
    print("=" * 60)

    print(f"\n📊 登录测试:")
    print(f"   状态: {'✅ 成功' if results['login_success'] else '❌ 失败'}")
    if results.get('checks', {}).get('initial_url'):
        print(f"   初始URL: {results['checks']['initial_url']}")
    if results.get('checks', {}).get('after_login_url'):
        print(f"   登录后URL: {results['checks']['after_login_url']}")
    if results['login_screenshot']:
        print(f"   登录后截图: {results['login_screenshot']}")

    print(f"\n📋 成员管理页面测试:")
    print(f"   状态: {'✅ 成功' if results['member_page_success'] else '❌ 失败'}")
    if results['member_screenshot']:
        print(f"   成员列表截图: {results['member_screenshot']}")

    print(f"\n🔍 页面元素检查:")
    for check_name, check_result in results.get('checks', {}).items():
        if check_name not in ['initial_url', 'after_login_url']:
            status_icon = '✅' if check_result else '❌'
            print(f"   {status_icon} {check_name}: {'存在' if check_result else '未找到'}")

    if results.get('errors'):
        print(f"\n⚠️  错误信息:")
        for i, error in enumerate(results['errors'], 1):
            print(f"   {i}. {error}")

    print(f"\n📸 截图文件:")
    screenshots = []
    if results.get('login_screenshot'):
        screenshots.append(results['login_screenshot'])
    if results.get('member_screenshot'):
        screenshots.append(results['member_screenshot'])

    if screenshots:
        for ss in screenshots:
            print(f"   - {ss}")
    else:
        print("   无截图生成")

    print("\n" + "=" * 60)
    print("测试完成")
    print("=" * 60)

    return results


if __name__ == "__main__":
    asyncio.run(main())
