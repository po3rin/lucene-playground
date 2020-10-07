lucene-playground

luceneの特定のテストを回す

```bash
./gradlew -p lucene/highlighter test --tests "*HighlighterTest.testQueryScorerHits"

# cache
./gradlew --stop
```
