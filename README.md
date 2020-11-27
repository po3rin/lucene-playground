lucene-playground

luceneの特定のテストを回す

```bash
./gradlew -p lucene/highlighter test --tests "*HighlighterTest.testQueryScorerHits"

# cache
./gradlew --stop
```

blog
https://po3rin.com/blog/try-lucene
https://blog.hatena.ne.jp/m3tech/m3tech.hatenablog.com/edit?entry=26006613629273309
