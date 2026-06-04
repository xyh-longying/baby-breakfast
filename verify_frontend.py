"""
验证 http://localhost:5174 前端可访问性
- 登录流程
- 成员管理页面验证
- 截图保存
"""

from playwright.sync_api import sync_playwright
import time


def verify_frontend():
    with sync_playwright() as p:
        # 1. 启动 headless chromium
        browser = p.chromium.launch(headless=True)
        page = browser.new_page()

        try:
            # 2. 导航到登录页面
            print("正在导航到登录页面...")
            page.goto("http://localhost:5174/login", wait_until="networkidle")
            print(f"✓ 登录页面加载完成: {page.url}")

            # 4. 输入用户名和密码
            print("正在输入登录信息...")
            page.fill('input[type="text"], input[name="username"], input[placeholder*="用户"]', "admin")
            page.fill('input[type="password"], input[name="password"], input[placeholder*="密码"]', "admin123")
            print("✓ 用户名和密码已输入")

            # 5. 点击登录按钮
            print("正在点击登录按钮...")
            page.click('button[type="submit"], button:has-text("登录"), button:has-text("登 录")')
            print("✓ 已点击登录按钮")

            # 6. 等待跳转完成
            print("等待跳转...")
            time.sleep(2)
            page.wait_for_load_state("networkidle")
            current_url = page.url
            print(f"✓ 跳转完成，当前URL: {current_url}")

            # 7. 导航到成员列表页
            print("正在导航到成员管理页面...")
            page.goto("http://localhost:5174/member/list", wait_until="networkidle")
            print(f"✓ 成员管理页面加载完成: {page.url}")

            # 9. 截图保存
            screenshot_path = "/tmp/verify_member.png"
            page.screenshot(path=screenshot_path, full_page=True)
            print(f"✓ 截图已保存到: {screenshot_path}")

            # 10. 打印页面标题和检查文字
            title = page.title()
            content = page.content()

            has_member_text = "成员管理" in content

            print("\n" + "=" * 50)
            print("验证结果:")
            print("=" * 50)
            print(f"页面标题: {title}")
            print(f"当前URL: {page.url}")
            print(f"包含'成员管理'文字: {'✓ 是' if has_member_text else '✗ 否'}")
            print("=" * 50)

            return {
                "success": True,
                "title": title,
                "url": page.url,
                "has_member_text": has_member_text,
                "screenshot": screenshot_path
            }

        except Exception as e:
            print(f"\n✗ 验证失败: {str(e)}")
            return {"success": False, "error": str(e)}

        finally:
            # 11. 关闭浏览器
            browser.close()
            print("\n浏览器已关闭")


if __name__ == "__main__":
    result = verify_frontend()
    if result.get("success"):
        print("\n🎉 前端验证通过!")
    else:
        print(f"\n❌ 前端验证失败: {result.get('error')}")
