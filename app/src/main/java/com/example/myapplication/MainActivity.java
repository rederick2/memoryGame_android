package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import androidx.cardview.widget.CardView;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int NUM_CARDS = 16;
    private List<String> symbols;
    private List<Integer> cardIndexes;
    private int flippedCards;
    private int foundPairs;
    private CardView firstCard;
    private CardView secondCard;
    private GridLayout gridLayout;
    private int firstIndex;
    private int secondIndex;
    private String firstSymbol;
    private String secondSymbol;
    private Set<Integer> matchedIndices;
    private TextView tvPairsFound;
    private TextView tvPairsRemaining;

    private static final String TAG = "MemoryGame";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvPairsFound = findViewById(R.id.tvPairsFound);
        tvPairsRemaining = findViewById(R.id.tvPairsRemaining);

        gridLayout = findViewById(R.id.gridLayout);


        symbols = generateSymbols();
        cardIndexes = new ArrayList<>();
        for (int i = 0; i < NUM_CARDS; i++) {
            cardIndexes.add(i);
        }
        Collections.shuffle(cardIndexes);

        matchedIndices = new HashSet<>(); // Inicializar el conjunto

        createGameBoard();

        flippedCards = 0;
        foundPairs = 0;
        firstCard = null;
        secondCard = null;

        updatePairStatus();
    }

    private List<String> generateSymbols() {
        List<String> symbols = new ArrayList<>();
        symbols.add("A");
        symbols.add("A");
        symbols.add("B");
        symbols.add("B");
        symbols.add("C");
        symbols.add("C");
        symbols.add("D");
        symbols.add("D");
        symbols.add("E");
        symbols.add("E");
        symbols.add("F");
        symbols.add("F");
        symbols.add("G");
        symbols.add("G");
        symbols.add("H");
        symbols.add("H");
        return symbols;
    }

    private void createGameBoard() {
        gridLayout.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < NUM_CARDS; i++) {
            View cardView = inflater.inflate(R.layout.card_item, gridLayout, false);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(i % 4, 1f);
            params.rowSpec = GridLayout.spec(i / 4, 1f);
            params.setMargins(8,8,8,8);
            cardView.setLayoutParams(params);
            cardView.setBackgroundColor(Color.DKGRAY);
            cardView.setOnClickListener(new CardClickListener(i));
            gridLayout.addView(cardView);
        }
    }

    private class CardClickListener implements View.OnClickListener {

        private int index;

        CardClickListener(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            try {
                if (matchedIndices.contains(index)) {
                    // Ignorar clics en tarjetas que ya han hecho match
                    Log.e(TAG, String.valueOf(index));
                    return;
                }
                CardView clickedCard = (CardView) v;
                TextView cardText = clickedCard.findViewById(R.id.card_text);

                int actualIndex = cardIndexes.get(index);
                String symbol = symbols.get(actualIndex);

                Log.d(TAG, "Clicked card index: " + index);
                Log.d(TAG, "Clicked card symbol: " + symbol);

                if (flippedCards == 0) {
                    flippedCards++;
                    firstCard = clickedCard;
                    firstIndex = index;
                    firstSymbol = symbol;
                    Log.d(TAG, String.valueOf(firstIndex));
                    cardText.setText(symbol);
                    cardText.setVisibility(View.VISIBLE);
                    firstCard.setBackgroundColor(Color.WHITE);
                } else if (flippedCards == 1) {
                    if (clickedCard == firstCard) {
                        return;
                    }

                    flippedCards++;
                    secondCard = clickedCard;
                    secondIndex = index;
                    secondSymbol = symbol;
                    Log.d(TAG, String.valueOf(secondIndex));
                    cardText.setText(symbol);
                    cardText.setVisibility(View.VISIBLE);
                    secondCard.setBackgroundColor(Color.WHITE);

                    //int firstIndex = cardIndexes.indexOf(gridLayout.indexOfChild(firstCard));
                    //int secondIndex = cardIndexes.indexOf(gridLayout.indexOfChild(secondCard));

                    Log.d(TAG, "primera carta: " + symbols.get(firstIndex) + " - " + firstIndex);
                    Log.d(TAG, "segunda carta: " + symbols.get(secondIndex) + " - " + secondIndex);

                    if (!firstSymbol.equals(secondSymbol)) {
                        // Si no coinciden, voltear de nuevo después de un segundo
                        gridLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (firstCard != null && secondCard != null) {
                                    TextView firstCardText = firstCard.findViewById(R.id.card_text);
                                    TextView secondCardText = secondCard.findViewById(R.id.card_text);

                                    firstCard.setBackgroundColor(Color.DKGRAY);
                                    firstCardText.setVisibility(View.INVISIBLE);
                                    secondCard.setBackgroundColor(Color.DKGRAY);
                                    secondCardText.setVisibility(View.INVISIBLE);
                                }
                                resetCards();
                            }
                        }, 1000);
                    } else {
                        matchedIndices.add(firstIndex);
                        firstCard.setBackgroundColor(Color.GRAY);
                        matchedIndices.add(secondIndex);
                        secondCard.setBackgroundColor(Color.GRAY);

                        Log.e(TAG, matchedIndices.toString());

                        foundPairs++;
                        updatePairStatus();

                        if (foundPairs == NUM_CARDS / 2) {
                            // Juego completado
                            Toast.makeText(MainActivity.this, "¡Juego completado!", Toast.LENGTH_SHORT).show();
                        }
                        resetCards();
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in CardClickListener: " + e.getMessage(), e);
            }
        }

        private void resetCards() {
            flippedCards = 0;
            firstCard = null;
            secondCard = null;
        }
    }

    private void updatePairStatus() {
        tvPairsFound.setText("Parejas encontradas: " + foundPairs);
        tvPairsRemaining.setText("Parejas restantes: " + ((NUM_CARDS / 2) - foundPairs));
    }
}