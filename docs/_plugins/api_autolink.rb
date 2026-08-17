# 本文中の `connectToLastDevice` のようなインラインコードを、API リファレンスの
# 該当ページへのリンクに変換する。書き手はバッククォートで囲むだけでよい。
#
# 対応表は _data/api_links.yml（scripts/gen-api-docs.py が SPEC から生成する）。
module SaberaApiAutolink
  # `foo`、`foo()`、`Type.foo(bar: Baz)` のいずれかにマッチし、名前を取り出す
  NAME = /\A(?:([A-Za-z_]\w*)\.)?([A-Za-z_]\w*)\s*(?:\([^()]*\))?\z/

  # <pre> と <a> の中は触らない。前者はコード例、後者は既にリンクになっている
  SKIP = %r{<pre\b.*?</pre>|<a\b.*?</a>}m

  CODE = %r{<code\b[^>]*>([^<]+)</code>}

  def self.apply(html, links, baseurl, self_url)
    guarded(html) do |chunk|
      chunk.gsub(CODE) do |tag|
        url = lookup(links, Regexp.last_match(1))
        url.nil? || url == self_url ? tag : %(<a href="#{baseurl}#{url}">#{tag}</a>)
      end
    end
  end

  def self.lookup(links, text)
    md = NAME.match(text.strip)
    return nil if md.nil?

    qualified = md[1] ? "#{md[1]}.#{md[2]}" : nil
    links[qualified] || links[md[2]]
  end

  # SKIP に当たる部分を除いた断片だけを yield して繋ぎ直す
  def self.guarded(html)
    out = +""
    rest = html
    while (md = SKIP.match(rest))
      out << yield(md.pre_match) << md[0]
      rest = md.post_match
    end
    out << yield(rest)
  end
end

Jekyll::Hooks.register [:pages, :documents], :post_render do |page|
  links = page.site.data["api_links"]
  next if links.nil? || page.output.nil?
  next unless page.output.include?("<code")

  baseurl = page.site.config["baseurl"].to_s
  page.output = SaberaApiAutolink.apply(page.output, links, baseurl, page.url)
end
