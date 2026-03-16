import { useState, useEffect } from "react";
import "./App.css";

const COLORS = ["All", "Blue", "Green", "Red", "Yellow", "Purple", "Neutral", "Multi Color"];

function App() {
  const [cards, setCards] = useState([]);
  const [deck, setDeck] = useState([]);
  const [selectedColor, setSelectedColor] = useState("All");
  const [loading, setLoading] = useState(true);
  const [visibleCount, setVisibleCount] = useState(30);
  const [search, setSearch] = useState("");

  // Fetch cards from backend when color filter changes
  useEffect(() => {
    setLoading(true);
    setVisibleCount(30);
    setSearch("");
    const url = selectedColor === "All" || selectedColor === "Multi Color"
      ? "/cards"
      : `/cards?color=${selectedColor}`;

    fetch(url)
      .then(res => res.json())
      .then(data => {
        if (selectedColor === "Multi Color") {
          setCards(data.filter(card => card.colors.length > 1));
        } else {
          setCards(data);
        }
        setLoading(false);
      });
  }, [selectedColor]);

  const addToDeck = (card) => {
    const count = deck.filter(c => c.id === card.id).length;
    if (count >= 3) return;
    setDeck([...deck, card]);
  };

  const removeFromDeck = (cardId) => {
    const index = deck.findLastIndex(c => c.id === cardId);
    if (index !== -1) {
      const newDeck = [...deck];
      newDeck.splice(index, 1);
      setDeck(newDeck);
    }
  };

  const deckCount = (cardId) => deck.filter(c => c.id === cardId).length;

  const uniqueDeckCards = [...new Map(deck.map(c => [c.id, c])).values()];

  const filteredCards = cards.filter(card =>
    card.id.toLowerCase().includes(search.toLowerCase())
  );

  const handleDownload = () => {
    const cardIds = deck.map(c => c.id);
    fetch("/download-pdf", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(cardIds)
    })
      .then(res => res.blob())
      .then(blob => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = url;
        a.download = "deck.pdf";
        a.click();
        window.URL.revokeObjectURL(url);
      });
  };

  return (
    <div className="app">

      {/* Sidebar */}
      <div className="sidebar">
        <h1>TESL Deck Builder</h1>

        <h2>Filter by Color</h2>
        <div className="color-filters">
          {COLORS.map(color => (
            <button
              key={color}
              className={`filter-btn ${selectedColor === color ? "active" : ""}`}
              onClick={() => setSelectedColor(color)}
            >
              {color}
            </button>
          ))}
        </div>

        <h2>Your Deck ({deck.length} cards)</h2>
        {deck.length < 50 && (
          <p className="warning">⚠️ Need {50 - deck.length} more cards (minimum 50)</p>
        )}
        <div className="deck-list">
          {uniqueDeckCards.map(card => (
            <div key={card.id} className="deck-item">
              <img src={card.imagePath} alt={card.id} />
              <div className="deck-item-info">
                <span>{card.id}</span>
                <span className="deck-count">x{deckCount(card.id)}</span>
              </div>
              <div className="deck-item-buttons">
                <button onClick={() => addToDeck(card)}>+</button>
                <button onClick={() => removeFromDeck(card.id)}>-</button>
              </div>
            </div>
          ))}
        </div>

        {deck.length >= 50 && (
          <button className="download-btn" onClick={handleDownload}>
            Download PDF
          </button>
        )}
      </div>

      {/* Main card grid */}
      <div className="main">
        <div className="search-bar">
          <input
            type="text"
            placeholder="Search cards..."
            value={search}
            onChange={e => {
              setSearch(e.target.value);
              setVisibleCount(30);
            }}
          />
        </div>
        {loading ? (
          <p>Loading cards...</p>
        ) : (
          <div className="card-grid">
            {filteredCards.slice(0, visibleCount).map(card => (
              <div key={card.id} className="card-item">
                <img src={card.imagePath} alt={card.id} />
                <div className="card-overlay">
                  <button onClick={() => addToDeck(card)}>
                    {deckCount(card.id) > 0 ? `Add (${deckCount(card.id)}/3)` : "Add to Deck"}
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
        {visibleCount < filteredCards.length && (
          <button className="load-more" onClick={() => setVisibleCount(v => v + 30)}>
            Load More ({filteredCards.length - visibleCount} remaining)
          </button>
        )}
      </div>

    </div>
  );
}

export default App;