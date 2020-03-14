import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Evaluation {

	public static final int treeNumber = 4;
	public static final int fruitStartNumber = 4;
	public static final int ravenGoal = 6;
	public static final int nbIterations = 10_000_000;
	public static final Random r = new Random();

	public static void main(String[] args) {

		List<Strategy> strategies = new ArrayList<>();
		strategies.add(new BestStrategy());
		strategies.add(new ChildrenStrategy());
		strategies.add(new FirstAvailableTreeStrategy());
		strategies.add(new BadStrategy());

		for (Strategy strategy : strategies) {

			int nbWonGame = 0;

			for (int i = 0; i < nbIterations; i++) {

				GameState gs = new GameState();
				int gameResult = 0;

				while (gameResult == 0) {

					int dice = r.nextInt(6);

					switch (dice) {
					case 0:
					case 1:
					case 2:
					case 3:
						if (gs.trees[dice] > 0) {
							gs.trees[dice]--;
						}
						break;
					case 4:
						gs.ravenProgress--;
						break;
					case 5:
						int choice = strategy.getChoice(gs);
						gs.trees[choice]--;
						break;
					default:
					}

					gameResult = isFinished(gs);

				}

				if (gameResult == 1) {
					nbWonGame++;
				}

			}

			System.out.println("Played " + nbIterations + " games with strategy " + String.format("%1$30s", strategy.getClass().getName()) + " and won " + String.format("%8d", nbWonGame) + " games: "
					+ String.format("%02.2f", 100 * nbWonGame / (double) nbIterations) + " %");
		}

	}

	// 0: game not finished / 1: game finished and won / -1: game finished and lost
	private static int isFinished(GameState gs) {
		int result = 1;

		if (gs.ravenProgress == 0) {
			result = -1; // We lost
		} else {
			for (int i : gs.trees) {
				if (i != 0) {
					result = 0; // Game not finished
					break;
				}
			}
		}

		return result;
	}

}

class GameState {

	public int[] trees;
	public int ravenProgress;

	public GameState() {
		super();
		trees = new int[Evaluation.treeNumber];
		Arrays.fill(trees, Evaluation.fruitStartNumber);
		ravenProgress = Evaluation.ravenGoal;
	}

}

abstract class Strategy {
	public abstract int getChoice(GameState gs);
}

// Always takes from the tree have the most remaining fruits
class BestStrategy extends Strategy {

	@Override
	public int getChoice(GameState gs) {
		int choice = 0;
		int maxFruit = gs.trees[0];

		for (int i = 1; i < gs.trees.length; i++) {
			if (gs.trees[i] > maxFruit) {
				maxFruit = gs.trees[i];
				choice = i;
			}
		}

		return choice;
	}

}

// Always takes from the tree have the less remaining fruits
class BadStrategy extends Strategy {

	@Override
	public int getChoice(GameState gs) {
		int choice = -1;
		int minFruit = Evaluation.fruitStartNumber + 1;

		for (int i = 0; i < gs.trees.length; i++) {
			if (gs.trees[i] < minFruit && gs.trees[i] > 0) {
				minFruit = gs.trees[i];
				choice = i;
			}
		}

		return choice;
	}

}

// Plays randomly. Yes I know, I'm not being nice to childrens here.
class ChildrenStrategy extends Strategy {

	private List<Integer> choices = new ArrayList<>();

	@Override
	public int getChoice(GameState gs) {
		int choice = 0;
		choices.clear();

		for (int i = 0; i < gs.trees.length; i++) {
			if (gs.trees[i] > 0) {
				choices.add(i);
			}
		}

		choice = choices.get(Evaluation.r.nextInt(choices.size()));

		return choice;
	}

}

// Always takes from the first tree still having fruits
class FirstAvailableTreeStrategy extends Strategy {

	@Override
	public int getChoice(GameState gs) {
		int choice = -1;

		for (int i = 0; i < gs.trees.length; i++) {
			if (gs.trees[i] > 0) {
				choice = i;
				break;
			}
		}

		return choice;
	}

}
