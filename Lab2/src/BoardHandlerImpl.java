import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.rmi.RemoteException;
import java.util.*;

public class BoardHandlerImpl implements IBoardHandler {

	private Map<String, String> lastSelectedUserMapToGameId = new HashMap<>();
	private List<IUser> listOfUsers = new ArrayList<>();
	private Multimap<String, IUser> ownersOfSymbols = ArrayListMultimap.create();
	private final List<String> availableSymbolsToTake = Arrays.asList("X", "Y");
	private Map<String, IBoardListener> userListenerMap = new HashMap<>();
	private Map<String, Board> gameIdToBoardMap = new HashMap<>();
	private List<IUser> userCompetingWithComputer = new ArrayList<>();
	private Map<String, Boolean> gameIdWithComputerToIfLastMove = new HashMap<>();

	@Override
	public String register(IUser user, IBoardListener listener, String gameId, boolean competingWithComputer)
			throws RemoteException, UserRejectedException {
		System.out.println(competingWithComputer);
		if (competingWithComputer) {
			userCompetingWithComputer.add(user);
			Board board = new Board();
			gameIdToBoardMap.put(gameId, board);
			gameIdWithComputerToIfLastMove.put(gameId, false);
			userListenerMap.put(user.getNick(), listener);
			System.out.println("Registering new game with computer!");
			return availableSymbolsToTake.get(0);
		} else {
			if (!userListenerMap.containsKey(user.getNick()) && getUsersWithinGroup(gameId).size() < 2) {
				userListenerMap.put(user.getNick(), listener);
				listOfUsers.add(user);
				String selectedSymbol = getFreeUserSymbol(user.getGameId());
				user.setSymbol(selectedSymbol);
				System.out.println(
						String.format("User %s is now registered with symbol %s, gameid %s", user.getNick(), user.getSymbol(),
								user.getGameId()));

				if (gameIdToBoardMap.get(gameId) == null) {
					Board board = new Board();
					gameIdToBoardMap.put(gameId, board);
				}
				ownersOfSymbols.put(selectedSymbol, user);

				return selectedSymbol;
			} else {
				return "";
			}
		}
	}

	@Override
	public void nextTurnInGameWithComputer(String gameId) throws RemoteException {
		Board board = gameIdToBoardMap.get(gameId);
		IUser userThatIsCompeting;

		for (IUser user : userCompetingWithComputer) {
			if (Objects.equals(user.getGameId(), gameId)) {
				userThatIsCompeting = user;
			}
		}

		if (board.detectIfTie() || board.detectIfWinner().isPresent()) {
			//end of game
		}

		//if user last move
		board.randomMove(availableSymbolsToTake.get(1));
		gameIdWithComputerToIfLastMove.put(gameId, true);
		//request move from player

		//if computer last move
		if (gameIdWithComputerToIfLastMove.get(gameId)) {
			requestMoveFromHumanInGameVersusComputer(gameId);
			gameIdWithComputerToIfLastMove.put(gameId, false);
		}

	}

	private void requestMoveFromHumanInGameVersusComputer(String gameId) throws RemoteException {
		IUser userInGame = null;
		for (IUser user : userCompetingWithComputer) {
			if (Objects.equals(user.getGameId(), gameId)) {
				userInGame = user;
			}
		}
		Board board = gameIdToBoardMap.get(gameId);
		String nick = userInGame.getNick();
		userListenerMap.get(nick).onYourTurnComputerGame(board.getFreeSpots(), board.getVisualRepresentation(), gameId);
	}

	private void nextTurn(String gameId) throws RemoteException {

		if (listOfUsers.stream().filter(user -> {
			try {
				return Objects.equals(user.getGameId(), gameId);
			} catch (RemoteException e) {
				return false;
			}
		}).count() == 2) {
			final String lastSelectedUser = lastSelectedUserMapToGameId.get(gameId);
			System.out.println("Next turn!");
			if (lastSelectedUser == null) {
				List<IUser> availableUsers = getUsersWithinGroup(gameId);
				final String nickOfSelectedUser = availableUsers.get(0).getNick();
				lastSelectedUserMapToGameId.put(gameId, nickOfSelectedUser);
			} else {
				List<IUser> availableUsers = getUsersWithinGroup(gameId);
				final String nickOfSelectedUser = !Objects.equals(availableUsers.get(0).getNick(), lastSelectedUser)
						? availableUsers.get(0).getNick() : availableUsers.get(1).getNick();
				lastSelectedUserMapToGameId.put(gameId, nickOfSelectedUser);
			}
			requestMoveFromClient(lastSelectedUserMapToGameId.get(gameId), gameId);
		}
	}

	private List<IUser> getUsersWithinGroup(String gameId) throws RemoteException {
		List<IUser> availableUsers = new ArrayList<>();
		for (IUser user : listOfUsers) {
			if (Objects.equals(user.getGameId(), gameId)) {
				availableUsers.add(user);
			}
		}
		return availableUsers;
	}

	@Override
	public void requestFirstMove(String gameId) {
		try {
			nextTurn(gameId);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendMove(IUser user, int order, String gameId, Boolean gameWithComputer) throws RemoteException {
		System.out.println("Move from " + user.getNick() + " registered. Processing...");
		final Board board = gameIdToBoardMap.get(gameId);
		board.performMove(user, order);
		Optional<String> winnerSymbol = board.detectIfWinner();
		if (winnerSymbol.isPresent()) {
			System.out.println("Winner symbol: " + winnerSymbol.get());
			sendMessageAboutWinnerToPlayers(gameId, winnerSymbol.get());
			return;
		}
		if (board.detectIfTie()) {
			sendMessageAboutTieToPlayers(gameId);
			return;
		}
		if (gameWithComputer) {
			nextTurnInGameWithComputer(gameId);
		} else {
			nextTurn(gameId);
		}
	}

	private void sendMessageAboutTieToPlayers(String gameId) throws RemoteException {
		List<String> userToBeSendAboutTie = new ArrayList<>();
		for (IUser user : listOfUsers) {
			if (Objects.equals(user.getGameId(), gameId)) {
				userListenerMap.get(user.getNick()).onTie();
			}
			System.out.println("Game ended!");
		}
		unregisterGroup(gameId);
	}

	private void sendMessageAboutWinnerToPlayers(String gameId, String winnerSymbol) throws RemoteException {
		List<String> usersToBeSendAboutWinning = new ArrayList<>();
		for (IUser user : listOfUsers) {
			if (Objects.equals(user.getGameId(), gameId)) {
				userListenerMap.get(user.getNick()).onGameEnd(winnerSymbol);
			}
			System.out.println("Game ended!");
		}
		unregisterGroup(gameId);
	}

	private void unregisterGroup(String gameId) throws RemoteException {
		List<String> nicksToBeRemoved = new ArrayList<>();

		for (IUser user : listOfUsers) {
			if (Objects.equals(user.getGameId(), gameId)) {
				nicksToBeRemoved.add(user.getNick());
				listOfUsers.remove(user);
			}
		}

		lastSelectedUserMapToGameId.remove(gameId);
		nicksToBeRemoved.stream().forEach(nick -> {
			ownersOfSymbols.removeAll(nick);
			userListenerMap.remove(nick);

		});

	}

	private void requestMoveFromClient(String selectedUser, String gameId) {
		System.out.println(String.format("Sending request to %s", selectedUser));
		try {
			final Board board = gameIdToBoardMap.get(gameId);
			userListenerMap.get(selectedUser).onYourTurn(board.getFreeSpots(), board.getVisualRepresentation(), gameId);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public String getFreeUserSymbol(String gameId) {
		List<String> takenSymbols = new ArrayList<>();
		for (Map.Entry<String, IUser> stringIUserEntry : ownersOfSymbols.entries()) {
			try {
				if (Objects.equals(stringIUserEntry.getValue().getGameId(), gameId)) {
					takenSymbols.add(stringIUserEntry.getKey());
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return availableSymbolsToTake.stream().filter(symbol -> !takenSymbols.contains(symbol)).findFirst().get();
	}
}
