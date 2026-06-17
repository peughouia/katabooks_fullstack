import { useState } from "react";


function App() {
  const [count, setCount] = useState(0);

  return (
    <>
      <div className="bg-background border border-border rounded-2xl p-4">
        <h2 className="text-primary-foreground bg-primary rounded-xl px-2 py-1">
          Titre
        </h2>
        <button className="bg-brand text-brand-foreground hover:bg-accent mt-4">
          Bouton d'action
        </button>
      </div>
    </>
  );
}

export default App;
