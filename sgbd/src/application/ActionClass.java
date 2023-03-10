package application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import entities.Cell;
import entities.OperatorCell;
import entities.TableCell;
import enums.OperationType;
import enums.OperationTypeEnums;
import gui.buttons.TypesButtons;
import gui.frames.ResultFrame;
import gui.frames.forms.FormFrameCreateTable;
import gui.frames.forms.FormFrameImportFile;
import gui.frames.forms.operations.FormFrameJuncao;
import gui.frames.forms.operations.FormFrameProjecao;
import gui.frames.forms.operations.FormFrameSelecao;
import util.ExportTable;

@SuppressWarnings("serial")
public class ActionClass extends JFrame implements ActionListener, MouseListener, KeyListener{
	
	private mxGraph graph;
	private mxGraphComponent graphComponent;
	private JPanel containerPanel;
	private Object parent;
	private Object newCell;
	private Boolean createCell=false;
	private String style;
	private String name;
	private Object jCell;
	private Boolean isOperation;
	private OperationType currentType;
	
	private TypesButtons tipoProjecao;
	private TypesButtons tipoSelecao;
	//private TypesButtons tipoProdutoCartesiano;
	//private TypesButtons tipoUniao;
	//private TypesButtons tipoDiferenca;
	//private TypesButtons tipoRenomeacao;
	private TypesButtons tipoJuncao;
	
	private Object newParent;
	//private JPanel edgePanel;
	
	private JButton edgeButton;
	private Boolean createEdge=false;
	
	private JButton deleteButton;
	private Boolean deleteCell = false;
	
	private JButton importButton;
	private JToolBar toolBar;
	
	private JButton saveTableButton;
	private JButton saveImageButton;
	private JButton saveGraphButton;
	
	private List<Cell> cells;
	private List<Cell> leafs;
	
	private TableCell currentTableCell = null;
	private JButton btnCreateTable;
	
	public ActionClass() {
		super("Jgraph teste");
		initGUI();
	}
	
	private void initGUI() {
		setSize(800,600);
		setLocationRelativeTo(null);
		
		graph = new mxGraph();
		graphComponent = new mxGraphComponent(graph);
		graphComponent.setPreferredSize(new Dimension(400,400));
		getContentPane().add(graphComponent);
		
	    containerPanel = new JPanel(new GridLayout(3, 1));
	    mxStylesheet stylesheet = graph.getStylesheet();
	    
	    tipoProjecao = new TypesButtons(stylesheet,"?? Projecao","projecao");
	    tipoProjecao.getButton().addActionListener(this);
	    containerPanel.add(tipoProjecao.getPanel());
	    
	    tipoSelecao = new TypesButtons(stylesheet,"?? Selecao","selecao");
	    tipoSelecao.getButton().addActionListener(this);
	    containerPanel.add(tipoSelecao.getPanel());
	    
	    tipoJuncao = new TypesButtons(stylesheet,"|X| Juncao","juncao");
	    tipoJuncao.getButton().addActionListener(this);
	    containerPanel.add(tipoJuncao.getPanel());
	    
	    /*
	    tipoProdutoCartesiano = new TypesButtons(stylesheet,"??? Produto Cartesiano","produtoCartesiano");
	    tipoProdutoCartesiano.getButton().addActionListener(this);
	    containerPanel.add(tipoProdutoCartesiano.getPanel());
	    
	    tipoUniao = new TypesButtons(stylesheet,"??? Uniao","uniao");
	    tipoUniao.getButton().addActionListener(this);
	    containerPanel.add(tipoUniao.getPanel());

	    tipoDiferenca = new TypesButtons(stylesheet,"- Diferenca","diferenca");
	    tipoDiferenca.getButton().addActionListener(this);
	    containerPanel.add(tipoDiferenca.getPanel());
	    
	    tipoRenomeacao = new TypesButtons(stylesheet,"?? Renomeacao","renomeacao");
	    tipoRenomeacao.getButton().addActionListener(this);
	    containerPanel.add(tipoRenomeacao.getPanel());
	    */
	    
		toolBar = new JToolBar();
		getContentPane().add(toolBar, BorderLayout.SOUTH);
	    
		importButton = new JButton("Importar tabela");
		toolBar.add(importButton);
		importButton.setHorizontalAlignment(SwingConstants.LEFT);
		importButton.addActionListener(this);
		
		btnCreateTable = new JButton("Criar tabela");
		toolBar.add(btnCreateTable);
		btnCreateTable.setHorizontalAlignment(SwingConstants.LEFT);
		btnCreateTable.addActionListener(this);
		
		edgeButton = new JButton("Edge");
		toolBar.add(edgeButton);
		edgeButton.setHorizontalAlignment(SwingConstants.LEFT);
	    edgeButton.addActionListener(this);
		
		deleteButton = new JButton("Delete Cell");
		toolBar.add(deleteButton);
		deleteButton.setHorizontalAlignment(SwingConstants.LEFT);
		deleteButton.addActionListener(this);
		
		saveTableButton = new JButton("Salvar tabela");
		toolBar.add(saveTableButton);
		saveTableButton.setHorizontalAlignment(SwingConstants.LEFT);
		saveTableButton.addActionListener(this);
		
		saveImageButton = new JButton("Salvar imagem");
		toolBar.add(saveImageButton);
		saveImageButton.setHorizontalAlignment(SwingConstants.LEFT);
		saveImageButton.addActionListener(this);
		
		saveGraphButton = new JButton("Salvar arvore");
		toolBar.add(saveGraphButton);
		saveGraphButton.setHorizontalAlignment(SwingConstants.LEFT);
		saveGraphButton.addActionListener(this);
		
	    
		this.add(containerPanel,BorderLayout.EAST);

		setVisible(true);
		
		parent = graph.getDefaultParent();
		graph.getModel().beginUpdate();
		
		mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
		layout.setUseBoundingBox(false);
		layout.execute(parent);
		
		this.cells = new ArrayList<>();
		this.leafs = new ArrayList<>();
		
		graphComponent.getGraphControl().addMouseListener(this);
		
		graphComponent.addKeyListener(this);
		
		graph.getModel().endUpdate();
			
	}

	private void assignVariables(String styleVar, String nameVar, boolean isOperation, OperationType currentType) {
		
		createCell = true;
		newCell = null;
		newParent = null;
		style = styleVar;
		name = nameVar;
		this.isOperation = isOperation;
		this.currentType = currentType;
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == tipoProjecao.getButton()) {
			
			assignVariables("projecao","??  projecao", true, OperationType.PROJECAO);
			
		}else if(e.getSource() == tipoSelecao.getButton() ) {
			
			assignVariables("selecao","??  selecao", true, OperationType.SELECAO);
			
		}/*else if(e.getSource() == tipoProdutoCartesiano.getButton()) {
			
			assignVariables("produtoCartesiano","???  produto cartesiano", true, OperationType.PRODUTO_CARTESIANO);
			
		}else if(e.getSource() == tipoUniao.getButton()) {
			
			assignVariables("uniao","???  uniao", true, OperationType.UNIAO);
			
		}else if(e.getSource() == tipoDiferenca.getButton()) {
			
			assignVariables("diferenca","-  diferenca", true, OperationType.DIFERENCA);
			
		}else if(e.getSource() == tipoRenomeacao.getButton()) {
			
			assignVariables("renomeacao","??  renomeacao", true, OperationType.RENOMEACAO);
			
		}*/else if(e.getSource() == tipoJuncao.getButton()) {
			
			assignVariables("juncao","|X| juncao", true, OperationType.JUNCAO);

		}else if(e.getSource() == edgeButton) {
			
			createEdge = true;
			
		}else if(e.getSource() == deleteButton) {
			
			deleteCell = true;
			
		}else if(e.getSource() == importButton) {
			
			TableCell tableCell = new TableCell(80, 30);
			
			Boolean deleteCell = false;
			AtomicReference<Boolean> deleteCellReference = new AtomicReference<>(deleteCell);
			
			new FormFrameImportFile(tableCell, deleteCellReference);
			
			if(!deleteCellReference.get()) {
				
				assignVariables(tableCell.getStyle(), tableCell.getName(), false, null);
				currentTableCell = tableCell;
			
			}else {
				
				tableCell = null;
				
			}
			
		}else if(e.getSource() == btnCreateTable) {
			
			TableCell tableCell = new TableCell(80, 30);
			
			AtomicReference<TableCell> tableCellReference = new AtomicReference<>(tableCell);
			
			new FormFrameCreateTable(tableCellReference);
			
			if(tableCellReference.get() != null) {
				
				assignVariables(tableCell.getStyle(), tableCell.getName(), false, null);
				currentTableCell = tableCell;
				
			}else {
				
				tableCell = null;
				
			}
			
		}else if(e.getSource() == saveTableButton) {	
			
			ExportTable.exportToCsv(cells.get(cells.size()-1).getContent(),"finalTable.csv");
			
		}else if(e.getSource() == saveImageButton) {
			
			ExportTable.exportToImage(this);
			
		}else if(e.getSource() == saveGraphButton) {
			
			ExportTable.saveGraph(cells, "GraphContent.csv");
			
		}
			
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
		if(currentTableCell == null) {
			return;
		}
		
		jCell = graphComponent.getCellAt(e.getX(), e.getY());
		Cell cell = cells.stream().filter(x -> x.getCell().equals((mxCell)jCell)).findFirst().orElse(null);
		
		if(createCell == true ) {

			newCell = graph.insertVertex(parent, null, name, e.getX(), e.getY(), 80, 30, style);
			
			if(!isOperation) {
				
				currentTableCell.setJGraphCell(newCell);
				cells.add(currentTableCell);
				currentTableCell.setX(e.getX());
				currentTableCell.setY(e.getY());
			
			}else {
				
				cells.add(new OperatorCell(name, style, newCell, currentType, e.getX(), e.getY(), 80, 30));
				
			}
			
			createCell = false;
			
		}
		
		if(jCell != null) {
			
			if(createEdge == true && newParent == null) {
				newParent = jCell;
			}
			
			Cell parentCell = newParent != null ? cells.stream().filter(x -> x.getCell().equals((mxCell)newParent)).findFirst().orElse(null) : null;
			
			if( createEdge == true && jCell != newParent) {
				
				graph.insertEdge(newParent, null,"", newParent, jCell);
				((mxCell) jCell).setParent((mxCell)newParent);
				
				cell.addParent(parentCell);
				
				if(parentCell != null) parentCell.setChild(cell);
				
				if(cell instanceof OperatorCell) {
					
					if(((OperatorCell)cell).getType() == OperationType.PROJECAO && cell.checkRules(OperationTypeEnums.UNARIA) == true) new FormFrameProjecao(jCell, cells);
						
					else if(((OperatorCell)cell).getType() == OperationType.SELECAO && cell.checkRules(OperationTypeEnums.UNARIA) == true) new FormFrameSelecao(jCell, cells);
						
					else if(((OperatorCell)cell).getType() == OperationType.JUNCAO && cell.getParents().size() == 2 && cell.checkRules(OperationTypeEnums.BINARIA) == true) new FormFrameJuncao(jCell, cells);
					
				}
				leafs.add(cell);
				leafs.remove(parentCell.getCell());
				
				newParent = null;
				createEdge = false;
				
			}
			
			if(deleteCell == true) {
				
				graph.getModel().remove(jCell);	
				deleteCell = false;
				
			}
		}

		if(e.getButton() == MouseEvent.BUTTON3 && jCell != null) {
			
			graph.getModel().remove(jCell);	

		}
		if(e.getButton() == MouseEvent.BUTTON2 && jCell != null) {
			
			cell.getSourceTableName(name);
			
		}
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {

		
	}

	@Override
	public void keyPressed(KeyEvent e) {

		Cell cell = jCell != null ? cells.stream().filter(x -> x.getCell().equals((mxCell)jCell)).findFirst().orElse(null) : null;
		
		if (e.getKeyCode() == KeyEvent.VK_S && jCell != null) {

			new ResultFrame(cell.getContent());
		
		}else if(e.getKeyCode() == KeyEvent.VK_DELETE && jCell != null) {
			
			graph.getModel().remove(jCell);	
		
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
