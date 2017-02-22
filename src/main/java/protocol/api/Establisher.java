package protocol.api;

import java.util.HashMap;

import model.entity.ElGamalKey;

public interface Establisher {
	/**
	 * Initialize the establisher with a contract
	 * @param c
	 */
	public void initialize(Contract<?,?,?,?> c, HashMap<ElGamalKey, String> u);
	
	/**
	 * Start establisher signature protocol
	 */
	public void start();
	
	/**
	 * Get the contract to be signed
	 * @return
	 */
	public Contract<?,?,?,?> getContract();
	
	/**
	 * Set the current wish of the owner of this establisher
	 * @param w
	 */
	public void setWish(Wish w);
	
	/**
	 * Get the current wish of the owner of this establisher
	 * @param w
	 */
	public Wish getWish(Wish w);
	
	/**
	 * Get the current status of the protocol
	 * @return
	 */
	public Status getStatus();
	
	/**
	 * Add a listener of establisher events
	 * @param l
	 */
	public void addListener(EstablisherListener l);
	
	/**
	 * Notify the listener of this establisher
	 */
	public void notifyListeners();
}