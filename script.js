function submitQuery() {
  const query = document.getElementById("queryInput").value.trim();
  const resultsDiv = document.getElementById("results");

  if (!query) {
    resultsDiv.innerHTML = "<p>Please enter a query.</p>";
    return;
  }

  resultsDiv.innerHTML = "<p>Loading results...</p>";

  fetch(`http://localhost:8080/search?query=${encodeURIComponent(query)}`)
    .then((res) => res.json())
    .then((data) => {
      resultsDiv.innerHTML = "";

      if (!data.items || data.items.length === 0) {
        resultsDiv.innerHTML = "<p>No results found.</p>";
        return;
      }

      data.items.forEach((item, index) => {
        const result = document.createElement("div");
        result.classList.add("result");

        result.innerHTML = `
          <h3>${item.title}</h3>
          <p>${item.snippet}</p>
          <a href="${item.link}" target="_blank">${item.link}</a>
        `;

        resultsDiv.appendChild(result);
      });
    })
    .catch((err) => {
      resultsDiv.innerHTML = "❌ Error fetching results.";
      console.error(err);
    });
}
