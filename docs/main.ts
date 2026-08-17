// Pages に移すまでの一時的な配信サーバー
import { serveDir } from "jsr:@std/http@1/file-server"

const fsRoot = "_site"

Deno.serve(async (req) => {
  const res = await serveDir(req, { fsRoot, quiet: true })
  if (res.status !== 404) return res

  // Jekyll は /getting-started を getting-started.html として出力する
  const url = new URL(req.url)
  if (url.pathname.endsWith("/") || url.pathname.includes(".")) return res

  const retry = new Request(`${url.origin}${url.pathname}.html`, req)
  const html = await serveDir(retry, { fsRoot, quiet: true })
  return html.status === 404 ? res : html
})
