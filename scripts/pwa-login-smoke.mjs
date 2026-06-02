import { chromium } from 'playwright';

const baseUrl = 'https://5173-4cdccab12b4fa09a.monkeycode-ai.online';

const browser = await chromium.launch({ headless: true });
const page = await browser.newPage();

try {
  await page.goto(baseUrl, { waitUntil: 'networkidle', timeout: 120000 });
  await page.waitForSelector('input[type="text"]', { timeout: 120000 });
  await page.fill('input[type="text"]', 'lingyun');
  await page.fill('input[type="password"]', 'baby-breakfast');
  await page.click('button:has-text("登录")');
  await page.waitForURL(`${baseUrl}/`, { timeout: 120000 });
  await page.waitForSelector('text=凌云一家', { timeout: 120000 });
  await page.waitForSelector('text=当前成员 凌云', { timeout: 120000 });

  const headerText = await page.locator('.subtitle').textContent();
  console.log(JSON.stringify({ success: true, headerText }));
} catch (error) {
  console.error(JSON.stringify({ success: false, message: error instanceof Error ? error.message : String(error) }));
  process.exitCode = 1;
} finally {
  await browser.close();
}
